package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.SheldonSandbekkhaug.Peace.CommonData;
import com.SheldonSandbekkhaug.Peace.EventType;
import com.SheldonSandbekkhaug.Peace.PacketMessage;
import com.SheldonSandbekkhaug.Peace.PeaceEntity;
import com.SheldonSandbekkhaug.Peace.Player;
import com.SheldonSandbekkhaug.Peace.Tile;

public class PeaceGameServer extends ApplicationAdapter {
	PeaceNetworkServer network;
	CommonData commonData;
	Queue<PacketMessage> events;
	
	// Player names that will join the next game. Index corresponds to playerID.
	ArrayList<String> lobby; 
	Random gen;
	
	@Override
	public void create () {
		int PORT = 27960;
		network = new PeaceNetworkServer(PORT);
		lobby = new ArrayList<String>();
		lobby.add("Neutral"); // Player 0 is not a user
		
		gen = new Random();
	}

	public void render () {
		// No need to render on server side
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		processEvents();
	}
	
	/* Process game events */
	public void processEvents()
	{
		// Get events from the network
		PacketMessage event = network.events.poll();
		while (event != null)
		{
			processNetworkEvent(event);
			event = network.events.poll();
		}
	}
	
	public void processNetworkEvent(PacketMessage pm)
	{
		PacketMessage reply;
		switch(pm.type)
		{
		case JOIN: // Client attempts to join the game
			lobby.add(pm.message);
			// Send a reply for their success
			reply = new PacketMessage();
			reply.type = EventType.JOIN;
			int playerID = lobby.size() - 1;
			network.sendToClient(reply, playerID);
			
			// TODO: send information about other players
			break;
		case START:
			out.println("Received request to start game");
			newGame(pm.message, lobby);
			break;
		case LEAVE:
			network.disconnected(pm.playerID);
			lobby.remove(pm.message);
			commonData.players.remove(pm.playerID);
			break;
		case STOP:
			commonData = null;
			break;
		case FROM_MARKET: // A Player bought an Entity
			buyEntity(pm.playerID, pm.message, pm.targetTileID);
			break;
		case MOVE: // A Player moved an Entity
			broadcastMoveEntity(pm.srcTileID, pm.targetTileID);
			commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			break;
		default:
			break;
		}
	}
	
	/* Initialize a new game according to the given skin. */
	public void newGame(String skin, ArrayList<String> playerNames)
	{		
		commonData = new CommonData(false); // Creates Unit table and applies Skin
		
		// Add Players to the game
		for (String name : playerNames)
		{
			commonData.players.add(new Player(name));
			
			// TODO: broadcast to clients
		}
		
		initializeMarket();
	}
	
	/*
	 *  Select n random Units from units and put them in the market ArrayList.
	 *  Note that this must be done on the server so that we can broadcast
	 *  market changes.
	 */
	private void initializeMarket()
	{
		int MARKET_SIZE = 5;
		
		for (int i = 0; i < MARKET_SIZE; i++)
		{
			String key = randomSelection(commonData.availableForMarket);
			PeaceEntity e = commonData.availableForMarket.get(key);
			commonData.addToMarket(e);
			commonData.availableForMarket.remove(key);
			
			// Create a TO_MARKET and broadcast it to the players
			PacketMessage event = new PacketMessage(e.getID());
			event.srcTileID = -1;
			event.type = EventType.TO_MARKET;
			network.broadcastToPlayers(event);
		}
	}
	
	/*
	 * Take the Entity off the market and add a new 
	 * Entity to the market
	 */
	public void buyEntity(int playerID, String entityID, int destTileID)
	{
		Player p = commonData.players.get(playerID);
		
		// TODO: check other EntityBanks for certain entities
		Tile t = commonData.getTileFromMarket(entityID);
		PeaceEntity e = t.getE();
		e.setOwner(playerID);

		// Subtract the cost of the Entity from the Player's funds
		p.setMoney(p.getMoney() - e.getCost());
		
		// Tell all players that P spent money
		PacketMessage moneyUpdate = new PacketMessage("money");
		moneyUpdate.type = EventType.PLAYER_UPDATE;
		moneyUpdate.playerID = playerID;
		moneyUpdate.number = p.getMoney();
		network.broadcastToPlayers(moneyUpdate);
		
		// Move the PeaceEntity from the market to its destination
		commonData.moveEntity(t.getTileID(), destTileID);
		
		// Update market. Have to create a "dummy" Entity to remove from Market
		PeaceEntity eClone = e.clone();
		commonData.removeFromMarket(eClone);
		
		// Tell all players which Entity was bought and its destination
		PacketMessage marketUpdate = new PacketMessage();
		marketUpdate.type = EventType.FROM_MARKET;
		marketUpdate.playerID = playerID;
		marketUpdate.srcTileID = t.getTileID();
		marketUpdate.targetTileID = destTileID;
		network.broadcastToPlayers(marketUpdate);
		
		// Select a random PeaceEntity from the availability HashMap
		String newEntityKey = randomSelection(commonData.availableForMarket);
		PeaceEntity newEntity = 
			commonData.availableForMarket.get(newEntityKey);
		
		// Remove that Entity from the availability HashMap
		commonData.availableForMarket.remove(newEntity.getID());
		
		// Add the PeaceEntity to the Market
		commonData.addToMarket(newEntity);
		
		// Tell all clients which Entity was added
		PacketMessage marketAdd = new PacketMessage(newEntity.getID());
		marketAdd.type = EventType.TO_MARKET;
		marketAdd.srcTileID = t.getTileID();
		network.broadcastToPlayers(marketAdd);
	}
	
	/* Select a random key from HashMap */
	public String randomSelection(HashMap<String, PeaceEntity> h)
	{
		int indexToTake = gen.nextInt(commonData.availableForMarket.size());
		
		int i = 0;
		for (String id : h.keySet())
		{
			if (i == indexToTake)
			{
				return id;
			}
			i++;
		}
		
		// Should never get to this point
		try
		{
			throw new Exception();
		}
		catch(Exception e)
		{
			System.out.println("Could not select a random entity from the Market.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Also should never get to this point
		return null;
	}
	
	/* Move the Entity at srcTileID to destTileID.
	 * Return true if successful, false otherwise.
	 */
	private boolean broadcastMoveEntity(int srcTileID, int destTileID)
	{
		if (commonData.moveEntity(srcTileID, destTileID))
		{
			PacketMessage reply = new PacketMessage();
			reply = new PacketMessage();
			reply.type = EventType.MOVE;
			reply.srcTileID = srcTileID;
			reply.targetTileID = destTileID;
			network.broadcastToPlayers(reply);
			return true;
		}
		else
		{
			System.out.println("Could not move PeaceEntity");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}

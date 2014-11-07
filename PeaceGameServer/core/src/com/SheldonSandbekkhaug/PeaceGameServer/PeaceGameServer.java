package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.SheldonSandbekkhaug.Peace.CommonData;
import com.SheldonSandbekkhaug.Peace.EventType;
import com.SheldonSandbekkhaug.Peace.PacketMessage;

public class PeaceGameServer extends ApplicationAdapter {
	PeaceNetworkServer network;
	CommonData commonData;
	Queue<PacketMessage> events;
	
	@Override
	public void create () {
		int PORT = 27960;
		network = new PeaceNetworkServer(PORT);
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
		switch(pm.type)
		{
		case JOIN: // Client attempts to join the game
			// Send a reply for their success
			PacketMessage reply = new PacketMessage();
			reply.type = EventType.JOIN;
			byte[] clientID = pm.clientID;
			network.sendToClient(clientID, reply);
			break;
		case START:
			out.println("Received request to start game");
			newGame(pm.message);
			break;
		case LEAVE:
			network.disconnected(pm.clientID);
			break;
		case STOP:
			commonData = null;
			break;
		case FROM_MARKET:
			// TODO: Remove an Entity from the Market
			break;
		default:
			break;
		}
	}
	
	/* Initialize a new game according to the given skin. */
	public void newGame(String skin)
	{		
		commonData = new CommonData(false); // Creates Unit table and applies Skin
		
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
		
		Random gen = new Random();
		
		ArrayList<String> unitKeys = new ArrayList<String>(commonData.units.keySet());
		
		for (int i = 0; i < MARKET_SIZE; i++)
		{
			// Select a Unit and remove it from the bank
			String key = unitKeys.get(gen.nextInt(unitKeys.size()));
			commonData.addToMarket(commonData.units.remove(key));
			unitKeys.remove(key);
			
			// Create a event and broadcast it to the players
			String message = key;
			PacketMessage event = new PacketMessage(message);
			event.type = EventType.TO_MARKET;
			
			out.println("Added " + key + " to the market on server");
			
			network.broadcastToPlayers(event);
		}
	}
}

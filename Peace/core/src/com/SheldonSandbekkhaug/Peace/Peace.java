package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Game;

/* Class for client-side game logic */
public class Peace extends Game {
	CommonData commonData;
	private PeaceNetworkClient network;
	int playerID; // The user's playerID
	int activePlayer; // playerID for whose turn it is
	
	@Override
	public void create () {		
		commonData = new CommonData(true);
		
		connectToServer("localhost");
		
		this.setScreen(new MainGameScreen(this));
	}
	
	/* Connect to a game server */
	public boolean connectToServer(String ipAddr)
	{	
		// TODO: Dynamically set network information
		int PORT = 27960;
		network = new PeaceNetworkClient();
		network.connect(5000, ipAddr, PORT);
		
		// Join a game on the server
		String playerName = "Primo";
		PacketMessage pm = new PacketMessage(playerName); // Player name
		pm.type = EventType.JOIN;
		network.sendToServer(pm, -1);
		
		// TODO: have the server assign active player number
		activePlayer = 1;
		
		// TODO: return false if join was unsuccessful
		return true;
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
		PeaceEntity e = null;
		switch(pm.type)
		{
		case JOIN: // Successfully added to the server's game
			// Now we know our player ID
			playerID = pm.playerID;
			
			// Start the game with the specified skin
			PacketMessage reply = new PacketMessage(commonData.skin);
			reply.type = EventType.START;
			network.sendToServer(reply, playerID);
			
			// TODO: wait for some other condition to start the game
			System.out.println("Client asked to start the game");
			break;
			// TODO: send/receive information about other players
		case TO_MARKET:
			// Add an Entity to the Market
			e = commonData.availableForMarket.get(pm.message);
			// TODO: generalize for other Entity types
			if (pm.srcTileID != -1)
			{
				Tile t = commonData.getTile(pm.srcTileID);
				t.setE(null);
			}
			commonData.addToMarket(e);
			break;
		case FROM_MARKET: // A Player bought something
			Tile src = commonData.getTile(pm.srcTileID);
			src.getE().setOwner(pm.playerID);
			commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			break;
		case PLAYER_UPDATE: // Update a Player field
			if (pm.message.equals("money"))
			{
				commonData.players.get(pm.playerID).setMoney(pm.number);
			}
			break;
		case MOVE: // Move a PeaceEntity
			commonData.moveEntity(pm.srcTileID, pm.targetTileID);
			break;
		default:
			break;
		}
	}
	
	/*
	 * Buy Entity e from the market for Player p. Tell the server the
	 * Entity will appear at the specified TileID.
	 * 
	 * This method should only be called by clients.
	 */
	public void buyEntity(PeaceEntity e, Player p, int tileID)
	{
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.FROM_MARKET;
		pm.playerID = p.getPlayerID();
		pm.targetTileID = tileID;
		pm.message = e.getID();
		
		network.sendToServer(pm, playerID);
	}
	
	/* Tell the server to move the PeaceEntity at srcTileID to destTileID */
	public void requestMoveEntity(int srcTileID, int destTileID)
	{
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.MOVE;
		pm.srcTileID = srcTileID;
		pm.targetTileID = destTileID;
		
		network.sendToServer(pm, playerID);
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose()
	{
		// TODO: Dispose textures
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.LEAVE;
		network.sendToServer(pm, playerID);
	}
}

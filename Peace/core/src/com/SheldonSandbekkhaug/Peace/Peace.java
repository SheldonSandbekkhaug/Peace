package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Game;

/* Class for client-side game logic */
public class Peace extends Game {
	CommonData commonData;
	private PeaceNetworkClient network;
	Player player; // The user
	
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
		network.sendToServer(pm);
		
		// TODO: do this only after the game is joined successfully
		player = new Player(playerName);
		commonData.players.add(player);
		
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
			// Start the game with the specified skin
			PacketMessage reply = new PacketMessage(commonData.skin);
			reply.type = EventType.START;
			network.sendToServer(reply);
			// TODO: wait for some other condition to start the game
			System.out.println("Client asked to start the game");
			break;
			// TODO: send/receive information about other players
		case TO_MARKET:
			// Add an Entity to the Market
			e = commonData.availableForMarket.get(pm.message);
			// TODO: generalize for other Entity types
			commonData.addToMarket(e);
			break;
		case FROM_MARKET: // A Player bought something
			Tile src = commonData.getTileFromMarket(pm.message);
			e = src.getE();
			e.setOwner(pm.playerID);
			Tile targetTile = commonData.getTile(pm.targetTileID);
			targetTile.setE(e);
			src.setE(null);
			break;
		case PLAYER_UPDATE:
			if (pm.message.equals("money"))
			{
				commonData.players.get(pm.playerID).setMoney(pm.number);
			}
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
		
		network.sendToServer(pm);
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose()
	{
		// TODO: Dispose textures
		PacketMessage pm = new PacketMessage(player.getName());
		pm.type = EventType.LEAVE;
		pm.playerID = player.getPlayerID();
		network.sendToServer(pm);
	}
}

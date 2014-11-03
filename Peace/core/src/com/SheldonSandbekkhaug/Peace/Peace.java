package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Game;

/* Class for client-side game logic */
public class Peace extends Game {
	CommonData commonData;
	private PeaceNetworkClient network;
	
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
		PacketMessage pm = new PacketMessage();
		pm.type = EventType.JOIN;
		network.sendToServer(pm);
		
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
		case TO_MARKET:
			// Add an Entity to the Market
			PeaceEntity e = commonData.units.get(pm.message);
			// TODO: generalize for structures
			commonData.market.add(e);
			break;
		case FROM_MARKET:
			// TODO: Remove an Entity from the Market
			break;
		default:
			break;
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	public void dispose()
	{
		// TODO: Dispose textures
	}
}

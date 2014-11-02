package com.SheldonSandbekkhaug.Peace;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryonet.Connection;

/* Class for server-side game logic. */
public class PeaceGameServer extends PeaceNetworkServer {
	CommonData commonData;
	
	public PeaceGameServer(int port) {
		super(port);
	}
	
	public static void main(String args[])
	{
		int PORT = 27960;
		PeaceGameServer gameServer = new PeaceGameServer(PORT);
		System.out.println("Server is up! Listening on port " + 
				gameServer.port);
	}
	
	/* This is run when we receive a packet */
	public void received(Connection c, Object object)
	{
		if (object instanceof PacketMessage)
		{
			PacketMessage pm = (PacketMessage)object;
			
			out.println("PGS receieved a message: " + pm.message);
			
			// Join or leave games
			if (pm.type == EventType.JOIN)
			{
				clientConnections.add(c);
				
				// Respond that the join was successful
				PacketMessage reply = new PacketMessage();
				reply.type = EventType.JOIN;
				c.sendTCP(reply);
			}
			else if (pm.type == EventType.START)
			{
				out.println("Received request to start game");
				newGame(pm.message);
			}
			else if (pm.type == EventType.LEAVE)
			{
				disconnected(c);
			}
		}
	}
	
	/* This is run when a client has disconnected */
	public void disconnected(Connection c)
	{
		clientConnections.remove(c);
		out.println("A client disconnected. Clients remaining: " + 
				clientConnections.size());
		if (clientConnections.size() <= 0)
		{
			// Close the game
			commonData = null;
		}
	}
	
	/* Initialize a new game according to the given skin. */
	public void newGame(String skin)
	{
		// TODO: remove
		/*
		boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();
		if (isLocAvailable)
			out.print("Local storage available");
		out.println("Files:");
		FileHandle[] files = Gdx.files.local(".").list();
		for(FileHandle file: files) {
		   // do something interesting here
			out.println(file.name() + " ");
		}
		*/
		
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
		commonData.market = new ArrayList<PeaceEntity>(MARKET_SIZE);
		
		Random gen = new Random();
		
		ArrayList<String> unitKeys = new ArrayList<String>(commonData.units.keySet());
		
		for (int i = 0; i < MARKET_SIZE; i++)
		{
			// Select a Unit and remove it from the bank
			String key = unitKeys.get(gen.nextInt(unitKeys.size()));
			commonData.market.add(commonData.units.get(key));
			commonData.units.remove(key);
			
			// Create a event and broadcast it to the players
			String message = key;
			PacketMessage event = new PacketMessage(message);
			event.type = EventType.TO_MARKET;
			
			broadcastToPlayers(event);
		}
	}
}

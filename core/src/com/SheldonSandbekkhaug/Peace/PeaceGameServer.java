package com.SheldonSandbekkhaug.Peace;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Random;

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
		
		// While the game is still running
		while (true)
		{
			// Process packets
			try {
				gameServer.broadcastToPlayers(new PacketMessage("Ping players"));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/* Initialize a new game according to the given skin. */
	public void newGame(String skin)
	{
		commonData = new CommonData(); // Creates Unit table and applies Skin
		
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
			
			String message = key + "to market";
			
			PacketMessage event = new PacketMessage(message);
			
			broadcastToPlayers(event);
		}
	}
	
	/* This is run when we receive a packet */
	public void received(Connection c, Object object)
	{
		if (object instanceof PacketMessage)
		{
			PacketMessage pm = (PacketMessage)object;
			
			out.println("PGS receieved a message: " + pm.message);
			
			// Join or leave games
			if (pm.message.equals("join"))
			{
				clientConnections.add(c);
			}
			else if (pm.message.equals("leave"))
			{
				clientConnections.remove(c);
			}
		}
	}
}

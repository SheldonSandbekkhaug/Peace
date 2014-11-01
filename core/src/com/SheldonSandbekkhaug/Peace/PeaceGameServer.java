package com.SheldonSandbekkhaug.Peace;

import static java.lang.System.out;

/* Class for server-side game logic. */
public class PeaceGameServer {
	PeaceNetworkServer network;
	
	public PeaceGameServer()
	{
		int PORT = 27960; // TODO: set dynamically?
		network = new PeaceNetworkServer(PORT);
		
		
	}
	
	public static void main(String args[])
	{
		PeaceGameServer gameServer = new PeaceGameServer();
		System.out.println("Server is up! Listening on port " + 
				gameServer.network.port);
		
		while (true)
		{
			
		}
	}
}

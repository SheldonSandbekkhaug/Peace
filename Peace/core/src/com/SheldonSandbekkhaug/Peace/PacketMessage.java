package com.SheldonSandbekkhaug.Peace;

public class PacketMessage {
	/* Anything in this class can be sent over the network */
	public int playerID; // Player number
	public String message; // General-purpose String
	public int number; // General-purpose integer
	public EventType type;
	
	public int srcTileID;
	public int targetTileID;
	
	public PacketMessage()
	{
		
	}
	
	public PacketMessage(String msg)
	{
		message = msg;
	}
}

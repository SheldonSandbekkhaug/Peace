package com.SheldonSandbekkhaug.Peace;

public class PacketMessage {
	/* Anything in this class can be sent over the network */
	public int playerID; // Player number
	public String message; // General-purpose String
	public int number; // General-purpose integer
	public EventType type;
	
	public int srcTileID;
	public int targetTileID;
	
	// For messages that add/remove Attributes
	public static final int ADD = 1;
	public static final int REMOVE = -1;
	
	public PacketMessage()
	{
		
	}
	
	public PacketMessage(String msg)
	{
		message = msg;
	}
}

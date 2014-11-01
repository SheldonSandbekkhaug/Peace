package com.SheldonSandbekkhaug.Peace;

public class PacketMessage {
	/* Anything in this class can be sent over the network */
	public String message;
	
	public LocationID srcLocation;
	public int srcTileNum;
	
	public LocationID targetLocation;
	public int targetTileNum;
}

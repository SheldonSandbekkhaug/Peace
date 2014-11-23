package com.SheldonSandbekkhaug.Peace;

import static java.lang.System.out; // TODO: remove?

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PeaceNetworkClient extends Listener {
	Client client;
	Queue<PacketMessage> events;
	
	public PeaceNetworkClient()
	{
		client = new Client();
		
		events = (Queue<PacketMessage>) new LinkedList<PacketMessage>();
		
		// Must register every class that will be sent/received
		client.getKryo().register(PacketMessage.class);
		client.getKryo().register(EventType.class);
		client.getKryo().register(LocationID.class);
		
		// Client must start before connecting can take place
		client.start();
		
		client.addListener(this);
		
		out.println("Client is now waiting..."); // TODO: remove
	}
	
	/* Wait is in milliseconds */
	public void connect(int wait, String ipAddr, int port)
	{
		try {
			client.connect(wait, ipAddr, port);
		} catch (IOException e) {
			out.println("Could not connect to " + ipAddr);
			e.printStackTrace();
		}
	}
	
	public void received(Connection c, Object obj)
	{
		if (obj instanceof PacketMessage)
		{
			out.println("Client recieved a PacketMessage:");
			PacketMessage pm = (PacketMessage)obj;
			out.println("type: " + pm.type + "; Message: " + pm.message);
			
			// Store this PacketMessage for the Game to read
			events.offer(pm);
		}
	}
	
	/* Send a message */
	public void sendMessage(String msg)
	{
		PacketMessage messageObj = new PacketMessage(msg);
		client.sendTCP((PacketMessage)messageObj);
	}
	
	public void sendToServer(PacketMessage pm, int playerID)
	{
		pm.playerID = playerID;
		client.sendTCP(pm);
	}
}

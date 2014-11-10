package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out; // TODO: remove?

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.SheldonSandbekkhaug.Peace.EventType;
import com.SheldonSandbekkhaug.Peace.LocationID;
import com.SheldonSandbekkhaug.Peace.PacketMessage;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class PeaceNetworkServer extends Listener {
	Server server;
	int port;
	HashMap<byte[], Connection> clients;
	public Queue<PacketMessage> events;
	
	public PeaceNetworkServer(int port) {
		server = new Server();
		
		// Maps MAC addresses (as client IDs) to Connections
		clients = new HashMap<byte[], Connection>();
		
		events = (Queue<PacketMessage>) new LinkedList<PacketMessage>();
		
		// Listen on a particular port
		this.port = port;
		try {
			server.bind(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Register a packet class. We can only send registered classes.
		server.getKryo().register(PacketMessage.class);
		server.getKryo().register(byte[].class);
		server.getKryo().register(EventType.class);
		server.getKryo().register(LocationID.class);
		
		server.start();
		
		server.addListener(this);
	}
	
	public static void main(String[] args) {
		int PORT = 27960;
		PeaceNetworkServer server = new PeaceNetworkServer(PORT);
		out.println("Server is up! Listening on port " + server.port);
	}
	
	/* Called when a connection is received */
	public void connected(Connection c)
	{
		// TODO: replace with real behavior
		out.println("Received a connection from " + c.getRemoteAddressTCP().getHostString());
		out.println("Their IP address: " + c.getRemoteAddressTCP().getAddress());
	}
	
	/* This is run when we receive a packet */
	public void received(Connection c, Object object)
	{
		// Print the message if it's a PacketMessage
		if (object instanceof PacketMessage)
		{
			PacketMessage pm = (PacketMessage)object;
			
			// TODO: remove print statements
			out.println("Receieved a message: " + pm.message);
			out.println("Type: " + pm.type);
			
			// Add this client to the connections
			if (pm.type == EventType.JOIN)
			{
				c.setName("" + clients.size());
				clients.put(pm.clientID, c);
			}
			
			events.offer(pm);
		}
	}
	
	/* This is run when a client has disconnected */
	public void disconnected(Connection c)
	{
		// Remove the connection from the client table
		for (byte[] key : clients.keySet())
		{
			if (clients.get(key) == c)
			{
				clients.remove(key);
			}
		}
		
		if (clients.size() <= 0)
		{
			// Close the game
			PacketMessage endGame = new PacketMessage();
			endGame.type = EventType.STOP;
			events.offer(endGame);
		}
	}
	
	public void disconnected(byte[] clientID)
	{
		Connection c = clients.get(clientID);
		disconnected(c);
	}
	
	/* Send pm to the client specified by clientID */
	public void sendToClient(byte[] clientID, PacketMessage pm)
	{
		Connection c = clients.get(clientID);
		c.sendTCP(pm);
	}
	
	/* Send this event to all players in this game */
	public void broadcastToPlayers(PacketMessage event)
	{
		//for (Connection c : clientConnections)
		for (Connection c: clients.values())
		{
			c.sendTCP(event);
		}
	}
}

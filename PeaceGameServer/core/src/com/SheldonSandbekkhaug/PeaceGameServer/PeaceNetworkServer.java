package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out; // TODO: remove?

import java.io.IOException;
import java.util.ArrayList;
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
	ArrayList<Connection> clients; // Indexed by playerID
	public Queue<PacketMessage> events;
	
	public PeaceNetworkServer(int port) {
		server = new Server();
		
		// Maps MAC addresses (as client IDs) to Connections
		clients = new ArrayList<Connection>();
		clients.add(null); // 0th index is reserved
		
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
				// Try to replace a null element in the client list
				boolean added = false;
				for (int i = 1; i < clients.size(); i++)
				{
					if (clients.get(i) == null)
					{
						clients.add(i, c);
						added = true;
						break;
					}
				}
				
				// Append to list if never replaced a null
				if (added == false)
				{
					clients.add(c);
				}
			}
			
			events.offer(pm);
		}
	}
	
	/* This is run when a client has disconnected */
	public void disconnected(Connection c)
	{
		// Remove the connection from the client table
		int numClients = 0;
		for (int i = 1; i < clients.size(); i++)
		{
			if (clients.get(i) == c)
			{
				clients.set(i, null);
				break;
			}
			numClients++;
		}
		
		if (numClients <= 0)
		{
			// Close the game
			PacketMessage endGame = new PacketMessage();
			endGame.type = EventType.STOP;
			events.offer(endGame);
		}
	}
	
	public void disconnected(int playerID)
	{
		Connection c = clients.get(playerID);
		
		
		
		disconnected(c);
	}
	
	/* Send pm to the client specified by clientID */
	//public void sendToClient(byte[] clientID, PacketMessage pm) // TODO: remove
	public void sendToClient(PacketMessage pm, int playerID)
	{
		Connection c = clients.get(playerID);
		c.sendTCP(pm);
	}
	
	/* Send this event to all players in this game */
	public void broadcastToPlayers(PacketMessage event)
	{
		//for (Connection c : clientConnections)
		for (Connection c: clients)
		{
			if (c != null)
				c.sendTCP(event);
		}
	}
}

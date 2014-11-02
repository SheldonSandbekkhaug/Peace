package com.SheldonSandbekkhaug.Peace;

import static java.lang.System.out; // TODO: remove?

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class PeaceNetworkServer extends Listener {
	Server server;
	int port;
	ArrayList<Connection> clientConnections;
	
	public PeaceNetworkServer(int port) {
		server = new Server();
		clientConnections = new ArrayList<Connection>();
		
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
		// TODO: replace with real behavior
		out.println("Receieved a message:");
		
		// Print the message if it's a PacketMessage
		if (object instanceof PacketMessage)
		{
			// TODO: replace with real behavior
			PacketMessage pm = (PacketMessage)object;
			out.println(pm.message);
		}
	}
	
	/* This is run when a client has disconnected */
	public void disconnected(Connection c)
	{
		clientConnections.remove(c);
		out.println("A client disconnected. Clients remaining: " + 
				clientConnections.size());
	}
	
	/* Send this event to all players in this game */
	protected void broadcastToPlayers(PacketMessage event)
	{
		for (Connection c : clientConnections)
		{
			c.sendTCP(event);
		}
	}
}

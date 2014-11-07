package com.SheldonSandbekkhaug.PeaceGameServer;

import static java.lang.System.out; // TODO: remove?

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
	HashMap<byte[], Connection> clientIDs;
	public Queue<PacketMessage> events;
	
	public PeaceNetworkServer(int port) {
		server = new Server();
		
		// Maps MAC addresses (as client IDs) to Connections
		clientIDs = new HashMap<byte[], Connection>();
		
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
		// TODO: replace with real behavior
		out.println("Receieved a message:");
		
		// Print the message if it's a PacketMessage
		if (object instanceof PacketMessage)
		{
			// TODO: replace with real behavior
			PacketMessage pm = (PacketMessage)object;
			out.println(pm.message);
			
			// Add this client to the connections
			if (pm.type == EventType.JOIN)
			{
				clientIDs.put(pm.clientID, c);
			}
			
			events.offer(pm);
		}
	}
	
	/* This is run when a client has disconnected */
	public void disconnected(Connection c)
	{
		// Get client's MAC address, which functions as its ID
		try {
			InetAddress clientIP = c.getRemoteAddressTCP().getAddress();
			byte[] clientID = NetworkInterface.getByInetAddress(clientIP)
					.getHardwareAddress();
			
			// Remove the client from our collection of clients
			clientIDs.remove(clientID);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		if (clientIDs.size() <= 0)
		{
			// Close the game
			PacketMessage endGame = new PacketMessage();
			endGame.type = EventType.STOP;
			events.offer(endGame);
		}
	}
	
	public void disconnected(byte[] clientID)
	{
		Connection c = clientIDs.get(clientID);
		disconnected(c);
	}
	
	/* Send pm to the client specified by clientID */
	public void sendToClient(byte[] clientID, PacketMessage pm)
	{
		Connection c = clientIDs.get(clientID);
		c.sendTCP(pm);
	}
	
	/* Send this event to all players in this game */
	public void broadcastToPlayers(PacketMessage event)
	{
		//for (Connection c : clientConnections)
		for (Connection c: clientIDs.values())
		{
			c.sendTCP(event);
		}
	}
}

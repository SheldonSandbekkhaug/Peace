import static java.lang.System.out;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class PeaceServer extends Listener {
	Server server;
	int port;
	
	public PeaceServer(int port) {
		server = new Server();
		
		// Listen on a particular port
		this.port = port;
		try {
			server.bind(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Register a packet class. We can only send registered classes.
		server.getKryo().register(PacketMessage.class);
		
		server.start();
		
		server.addListener(this);
	}
	
	public static void main(String[] args) {
		PeaceServer server = new PeaceServer(27960);
		out.println("Server is up!");
	}
	
	/* Called when a connection is received */
	public void connected(Connection c)
	{
		// TODO: replace with real behavior
		out.println("Received a connection from " + c.getRemoteAddressTCP().getHostString());
		out.println("Their IP address: " + c.getRemoteAddressTCP().getAddress());
		
		// TODO: remove
		// TESTING: send a packet
		PacketMessage pm = new PacketMessage();
		pm.message = "It begins.";
		
		c.sendTCP(pm); // Sends the message
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
		out.println("A client disconnected");
}
}

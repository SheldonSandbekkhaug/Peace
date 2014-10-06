import static java.lang.System.out;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PeaceClient extends Listener {

	static Client client;
	boolean messageReceived; // TODO: remove after testing
	
	// TODO: separate the client into its own program
	public static void main(String[] args) {
		int tcpPort = 27960; // TODO: don't hardcode the port numbers
		
		String ip = "localhost"; // IP address to connect to
		
		PeaceClient pClient = new PeaceClient();
		
		// Wait 5000ms before failing
		pClient.connect(5000, ip, tcpPort);
		
		// Wait for a message
		pClient.messageReceived = false;
		while(pClient.messageReceived == false)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//out.println("Client will now exit.");
		//System.exit(0);
	}
	
	public PeaceClient()
	{
		client = new Client();
		
		// Must register every class that will be sent/received
		client.getKryo().register(PacketMessage.class);
		
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
			out.println(pm.message);
			this.messageReceived = true;
		}
	}
	
}
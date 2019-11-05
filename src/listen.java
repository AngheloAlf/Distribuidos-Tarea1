import java.io.*;
import java.net.*;

class Listen extends Thread
{
	private static int PORT = 50000;
	private static String IP = "230.0.0.0";

	public void run() 
	{
		byte[] buffer = new byte[1024];
		try {

			MulticastSocket socket = new MulticastSocket(PORT);
			InetAddress group = InetAddress.getByName(IP);
			socket.joinGroup(group);

			while(true){
				Thread.sleep(1000);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
				if(message.substring(0, 1) != "C") { // es un mensaje del servidor y no del cliente
					System.out.println(message);
				}
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}

	}
}

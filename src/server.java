import java.io.*;
import java.net.*;

class server
{

	private static int PORT = 50000; // Must be inside range [49152 - 65535]
	private static String IP = "230.0.0.0";

	public static void sendUDPMessage(String message, String ipAddress) throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		byte[] messageBytes = message.getBytes();
		DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, group, PORT);
		socket.send(packet);
		socket.close();
	}

	public static void main(String args[])
	{
		if(args.length < 1) {
			System.out.println("ERROR: Debes especificar la IP!");
			System.out.println("Uso: java server <ip multicast>");
		}
		try {
			sendUDPMessage("Hola Anghelo", IP);
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
		System.out.println("Hola Alf");
	}
}

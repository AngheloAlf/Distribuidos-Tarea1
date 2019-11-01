import java.io.*;
import java.net.*;

class client
{
	private static int PORT = 50000;
	private static String IP = "230.0.0.0";

	public static void main(String args[])
	{
		if(args.length < 1) {
			System.out.println("ERROR: Debes especificar la IP!");
			System.out.println("Uso: java client <ip multicast>");
		}
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
				System.out.println("el mensaje de Multicast recibido dice: " + message);
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}

	}
}

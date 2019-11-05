import java.io.*;
import java.net.*;
import java.util.Scanner;

class client
{
	private static int PORT = 50000;
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
			System.out.println("Uso: java client <ip multicast>");
		}
		byte[] buffer = new byte[1024];

		try {
			// Aqui deberiamos obtener el client ID

			Listen listenThread = new Listen();
			listenThread.start();

			String comando;
			Scanner input = new Scanner(System.in);
			while(true){
				Thread.sleep(1000);
				comando = input.nextLine();
				sendUDPMessage(comando, IP);
				System.out.println("Mensaje enviado");
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}

	}
}

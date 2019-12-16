import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Listen extends Thread
{
	private static int PORT = 50000;
	private static String IP = "230.0.0.0";
	private static Pattern clientPattern = Pattern.compile("Client.*", Pattern.CASE_INSENSITIVE);

	public void run() 
	{
		byte[] buffer = new byte[1024];
		try {
			MulticastSocket socket = new MulticastSocket(PORT);
			InetAddress group = InetAddress.getByName(IP);
			socket.joinGroup(group);
			socket.setSoTimeout(1000*5); // Esperar hasta 5 segundos entre mensajes.
            try {
                while(true){
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    if(!clientPattern.matcher(message).matches()) { // es un mensaje del servidor y no del cliente
                        System.out.println(message);
                    }
                }
            } catch(SocketTimeoutException e){
                System.out.println("Se supero el tiempo de espera.");
                System.out.println("Presione enter para salir.");
            }
            socket.close();
        }
        catch(Exception e) {
			System.out.println(e.toString());
		}

	}
}

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Server
{

	private static int PORT = 50000; // Must be inside range [49152 - 65535]
	private static String IP = "230.0.0.0";
	private static Queue<Cancion> queue = new LinkedList<>();
	volatile private static String status = "playing"; //stopped, paused or playing
	volatile private static String nowPlaying = null;
	volatile private static Integer timeLeft = 0;
	private static Integer totalSongTime = 0;

	private static Pattern clientPattern = Pattern.compile("Client.*", Pattern.CASE_INSENSITIVE);
	private static Pattern pausePattern = Pattern.compile("Client.*_PAUSE_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern listPattern = Pattern.compile("Client.*_LIST_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern stopPattern = Pattern.compile("Client.*_STOP_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern nextPattern = Pattern.compile("Client.*_NEXT_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern historyPattern = Pattern.compile("Client.*_HISTORY_.*", Pattern.CASE_INSENSITIVE);

	private static Pattern playPattern = Pattern.compile(".*?_PLAY_(.*?)_(\\d+?)_(.*)", Pattern.CASE_INSENSITIVE);
	private static Pattern keewihPattern = Pattern.compile(".*?_KEEWIH_(.*?)_(\\d+?)_(.*?)", Pattern.CASE_INSENSITIVE);
	private static Pattern jumpPattern = Pattern.compile(".*?_JUMP_(\\d+)_(.*?)", Pattern.CASE_INSENSITIVE);


	private static Cancion cancion;

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
		} else {
			IP = args[0];
		}
		try {
			
			byte[] buffer = new byte[1024];

			MulticastSocket socket = new MulticastSocket(PORT);
			InetAddress group = InetAddress.getByName(IP);
			socket.joinGroup(group);

			Thread singer = new Thread() {
				public void run() {
					while(true) {
						try {
							Thread.sleep(1000);
							if(status.equals("playing")) {
								sendUDPMessage(nowPlaying + "_" + timeLeft.toString(), IP);
								timeLeft--;
							} else {
								sendUDPMessage(status, IP);
							}
							if(timeLeft == 0) {
								try {
									cancion = queue.remove();
									nowPlaying = cancion.nombre;
									timeLeft = cancion.timeLeft;
								} catch (NoSuchElementException e) {
									status = "stopped";
								}
							}
						} catch (Exception e) {
							System.out.println("Se ha hallado un error en el cantador:");
							System.out.println(e.toString());
						}
					}
				}
			};
			singer.start();
			while(true){
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
				if(clientPattern.matcher(message).matches()) { // es un mensaje del cliente
					System.out.print("Mensaje recibido por el servidor: ");
					System.out.println(message);
					Matcher playMatch = playPattern.matcher(message);
					Matcher keewihMatch = keewihPattern.matcher(message);
					Matcher jumpMatch = jumpPattern.matcher(message);
					if(playMatch.matches()) {
						System.out.println("ALOHA");
						while(queue.size() != 0) queue.remove(); // vaciamos la cola (Esta bien vaciarla?)
						System.out.println("ALO1");
						nowPlaying = playMatch.group(1); // Parece que esto de los grupos me tira error
						System.out.println("ALO2");
						timeLeft = Integer.parseInt(playMatch.group(2));
						totalSongTime = timeLeft;
						status = "playing";
						System.out.println("ALO3");
					} else if(stopPattern.matcher(message).matches()) {
						while(queue.size() != 0) queue.remove(); // vaciamos la cola (Esta bien vaciarla?)
						nowPlaying = null;
						timeLeft = 0;
						totalSongTime = 0;
						status = "stopped";
					} else if(pausePattern.matcher(message).matches()) {
						if(status.equals("paused")) {
							status = "playing";
						} else {
							status = "paused";
						}
					} else if(keewihMatch.matches()) {
						cancion = new Cancion();
						cancion.nombre = keewihMatch.group(1);
						cancion.timeLeft = Integer.parseInt(keewihMatch.group(2));
						queue.add(cancion);
					} else if(listPattern.matcher(message).matches()) {
						sendUDPMessage("Esta es la lista de canciones", IP); // Mostrar la lista de canciones
					} else if(nextPattern.matcher(message).matches()) {
						cancion = queue.remove();
						nowPlaying = cancion.nombre;
						timeLeft = cancion.timeLeft;
						totalSongTime = cancion.timeLeft;
					} else if(jumpMatch.matches()) {
						Integer number = Integer.parseInt(jumpMatch.group(1));
						if(number == 0) {
							timeLeft = totalSongTime;
						} else {
							if(number < 0){
								number = queue.size() + 1 - number;
							}
							while(number-- != 0) {
								cancion = queue.remove();
								nowPlaying = cancion.nombre;
								timeLeft = cancion.timeLeft;
							}
						}
					} else if(historyPattern.matcher(message).matches()) {
						sendUDPMessage("Esta es la lista de comandos", IP); // Mostrar la lista de comandos (que aun hay que construir)
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		
		System.out.println("Hola Alf");
	}
}

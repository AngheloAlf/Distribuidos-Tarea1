import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Server
{

	private static int PORT = 50000; // Must be inside range [49152 - 65535]
	private static String IP = "230.0.0.0";

	private static Queue<Cancion> queue = new LinkedList<>();
	volatile private static String status = "stopped"; //stopped, paused or playing
	volatile private static String nowPlaying = null;
	volatile private static Integer timeLeft = 0;
	private static Integer totalSongTime = 0;
	private static Integer commandID = 0;
	private static Integer nextClientId = 1;
	private static LinkedList<String> history = new LinkedList<String>();

	private static Pattern clientPattern = Pattern.compile("Client.*?_(.*)", Pattern.CASE_INSENSITIVE);
	private static Pattern pausePattern = Pattern.compile("Client.*_PAUSE_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern listPattern = Pattern.compile("Client.*_LIST_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern stopPattern = Pattern.compile("Client.*_STOP_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern nextPattern = Pattern.compile("Client.*_NEXT_.*", Pattern.CASE_INSENSITIVE);
	private static Pattern historyPattern = Pattern.compile("Client.*_HISTORY_.*", Pattern.CASE_INSENSITIVE);

	private static Pattern playPattern = Pattern.compile(".*?_PLAY_(.*?)_(\\d+?)_(.*)", Pattern.CASE_INSENSITIVE);
	private static Pattern keewihPattern = Pattern.compile(".*?_KEEWIH_(.*?)_(\\d+?)_(.*?)", Pattern.CASE_INSENSITIVE);
	private static Pattern jumpPattern = Pattern.compile(".*?_JUMP_(\\d+)_(.*?)", Pattern.CASE_INSENSITIVE);

	private static Pattern connectPattern = Pattern.compile("^CONNECT$", Pattern.CASE_INSENSITIVE);
	private static Pattern disconnectPattern = Pattern.compile("^Client([0-9]+)_EXIT_ID([0-9]+)$", Pattern.CASE_INSENSITIVE);

	private static Queue<Cancion> auxQueue = new LinkedList<>();
	private static Cancion cancion;

	public static void main(String args[])
	{
		if(args.length < 1) {
			System.out.println("ERROR: Debes especificar la IP!");
			System.out.println("Uso: java server <ip multicast>");
		} else {
			IP = args[0];
		}
		try {

			MulticastHelper socket = new MulticastHelper(IP, PORT);
			Thread singer = new Thread() {
				public void run() {
					while(true) {
						try {
							Thread.sleep(1000);
							if(status.equals("playing")) {
								socket.send(nowPlaying + "_" + timeLeft.toString());
								timeLeft--;
							} else {
								if(status.equals("paused")) {
									socket.send("Reproduccion pausada. Ingrese 'PAUSE' para reanudar la reproduccion");
								} else {
									socket.send(status);
								}
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
			while(true) {
				String message = socket.receive();
				Matcher clientMatch = clientPattern.matcher(message);
				if(clientMatch.matches()) { // es un mensaje del cliente
					System.out.println("Recv << " + message);
					socket.send("CCast_" + clientMatch.group(1));
					history.push(message);
					Matcher playMatch = playPattern.matcher(message);
					Matcher keewihMatch = keewihPattern.matcher(message);
					Matcher jumpMatch = jumpPattern.matcher(message);
					Matcher disconnectMatch = disconnectPattern.matcher(message);
					if(playMatch.matches()) {
						while(queue.size() != 0) queue.remove(); // vaciamos la cola (Esta bien vaciarla?)
						nowPlaying = playMatch.group(1); // Parece que esto de los grupos me tira error
						timeLeft = Integer.parseInt(playMatch.group(2));
						totalSongTime = timeLeft;
						status = "playing";
					} else if(stopPattern.matcher(message).matches()) {
						while(queue.size() != 0) queue.remove();
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
						socket.send("Esta es la lista de canciones en cola:");
						for(Integer i = 1; queue.size() != 0; i++) {
							cancion = queue.remove();
							socket.send("Cancion " + i.toString() + ": " + cancion.nombre + " - largo: " + cancion.timeLeft);
							auxQueue.add(cancion);
						}
						while(auxQueue.size() != 0) {
							queue.add(auxQueue.remove());
						}
						socket.send("Fin lista de canciones.");
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
							while(number-- > 0) {
								if(queue.size() > 0){
									cancion = queue.remove();
									nowPlaying = cancion.nombre;
									timeLeft = cancion.timeLeft;
								}
								else{
									status = "stopped";
								}
							}
						}
					} else if(historyPattern.matcher(message).matches()) {
						socket.send("Esta es la lista de comandos");
						for(Integer i = 0; i < history.size(); i++) {
							socket.send(history.get(i) + "_ID: " + i.toString());
						}
						socket.send("Fin lista de comandos.");
					} else if(disconnectMatch.matches()) {
						String clientId = disconnectMatch.group(1);
						String packetId = disconnectMatch.group(2);
					}
				} else if(connectPattern.matcher(message).matches()) {
					System.out.println("Recv << " + message);
					socket.send("CONNECT_CLIENT" + nextClientId++);
				}
			}

			// socket.close();
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());
		}
		
		System.out.println("Hola Vicente");
	}
}

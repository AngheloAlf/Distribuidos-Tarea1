import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

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
			while (true) {
				String message = socket.receive();
				MessageMatcher msgMatcher = new MessageMatcher(message);

				if (!msgMatcher.isFromClient()) {
					continue;
				}

				System.out.println("Recv << " + message);
				// socket.send("CCast_" + clientMatch.group(1));
				history.push(message);

				CommandData playData = msgMatcher.clientPlay();
				CommandData queueAddData = msgMatcher.clientQueueAdd();
				CommandData jumpData = msgMatcher.clientJump();			
	
				if (msgMatcher.clientConnect() != null) {
					socket.send("CONNECT_CLIENT" + nextClientId++);
				} else if (playData != null) {
					String[] cmdArgs = playData.getArgs();
					nowPlaying = cmdArgs[0];
					timeLeft = Integer.parseInt(cmdArgs[1]);
					totalSongTime = timeLeft;
					status = "playing";
				} else if (msgMatcher.clientStop() != null) {
					while(queue.size() != 0) queue.remove();
					nowPlaying = null;
					timeLeft = 0;
					totalSongTime = 0;
					status = "stopped";
				} else if (msgMatcher.clientPause() != null) {
					if (status.equals("paused")) {
						status = "playing";
					} else {
						status = "paused";
					}
				} else if (queueAddData != null) {
					String[] cmdArgs = queueAddData.getArgs();
					cancion = new Cancion();
					cancion.nombre = cmdArgs[0];
					cancion.timeLeft = Integer.parseInt(cmdArgs[1]);
					queue.add(cancion);
				} else if (msgMatcher.clientQueueList() != null) {
					socket.send("Esta es la lista de canciones en cola:");
					for(int i = 1; queue.size() != 0; i++) {
						cancion = queue.remove();
						socket.send("Cancion " + i + ": " + cancion.nombre + " - largo: " + cancion.timeLeft);
						auxQueue.add(cancion);
					}
					while(auxQueue.size() != 0) {
						queue.add(auxQueue.remove());
					}
					socket.send("Fin lista de canciones.");
				} else if (msgMatcher.clientNext() != null) {
					if (queue.size() > 0) {
						cancion = queue.remove();
						nowPlaying = cancion.nombre;
						timeLeft = cancion.timeLeft;
						totalSongTime = cancion.timeLeft;
					} else {
						nowPlaying = null;
						timeLeft = 0;
						totalSongTime = 0;
						status = "stopped";
					}
				} else if (jumpData != null) {
					String[] cmdArgs = jumpData.getArgs();
					int number = Integer.parseInt(cmdArgs[0]);

					if (number == 0) {
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
				} else if (msgMatcher.clientHistory() != null) {
					socket.send("Esta es la lista de comandos");
					for(int i = 0; i < history.size(); i++) {
						socket.send(history.get(i) + "_ID: " + i);
					}
					socket.send("Fin lista de comandos.");
				} else if (msgMatcher.clientDisconnect() != null) {

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

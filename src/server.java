import java.io.IOException;
import java.util.LinkedList;

class Server
{
	private static int PORT = 50000; // Must be inside range [49152 - 65535]
	private static String IP = "230.0.0.0";

	private static Integer commandID = 0;
	private static Integer nextClientId = 1;
	private static LinkedList<String> history = new LinkedList<String>();

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

			Singer singer = new Singer(socket);
			singer.start();

			boolean keepRunning = true;
			while (keepRunning && singer.isAlive()) {
				try {
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
						singer.play(new Song(cmdArgs[0], Integer.parseInt(cmdArgs[1])));
					} else if (msgMatcher.clientStop() != null) {
						singer.stopSinger();
					} else if (msgMatcher.clientPause() != null) {
						singer.pause();
					} else if (queueAddData != null) {
						String[] cmdArgs = queueAddData.getArgs();
						singer.queueAdd(new Song(cmdArgs[0], Integer.parseInt(cmdArgs[1])));
					} else if (msgMatcher.clientQueueList() != null) {
						socket.send("Esta es la lista de canciones en cola:");
						Song[] list = singer.queueList();
						for (int i = 0; i < list.length; ++i){
							socket.send("Cancion " + (i+1) + ": " + list[i].toString());
						}
						socket.send("Fin lista de canciones.");
					} else if (msgMatcher.clientNext() != null) {
						singer.next();
					} else if (jumpData != null) {
						String[] cmdArgs = jumpData.getArgs();
						singer.jump(Integer.parseInt(cmdArgs[0]));
					} else if (msgMatcher.clientHistory() != null) {
						socket.send("Esta es la lista de comandos");
						for(int i = 0; i < history.size(); i++) {
							socket.send(history.get(i) + "_ID: " + i);
						}
						socket.send("Fin lista de comandos.");
					} else if (msgMatcher.clientDisconnect() != null) {

					}
				} catch (IOException e) {
					e.printStackTrace(System.err);
					System.out.println(e.toString());

					System.out.println("Ocurrio un error inesperado en el traspaso de paquetes.");
					keepRunning = false;
					singer.keepRunning = false;
				}
			}
			
			socket.close();
		} catch(IOException e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());

			System.out.println("No fue posible unirse a la sesión Multicast.");
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());

			System.out.println("Error inesperado.");
		}

		System.out.println("Hola Vicente");
	}
}

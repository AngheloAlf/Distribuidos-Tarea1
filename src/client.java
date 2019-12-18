import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

class Client
{
	private static int PORT = 50000;
	private static String IP = "230.0.0.0";

	public static void main(String args[])
	{
		if(args.length < 1) {
			System.out.println("ERROR: Debes especificar la IP!");
			System.out.println("Uso: java client <ip multicast>");
		}

		try {
			MulticastHelper socket = new MulticastHelper(IP, PORT);
			socket.setTimeout(1000*5); // Esperar hasta 5 segundos entre mensajes.

			System.out.println("Conectando...");
			Listen listenThread = new Listen(socket);
			listenThread.start();

			String ID = "Client" + listenThread.clientId;
			System.out.println("Tu ID es <" + listenThread.clientId + ">");

			Scanner input = new Scanner(System.in);
			Boolean keepRunning = true;
			Integer commandId = 1;
			try {
				while(keepRunning && listenThread.isAlive()) {
					String comando = input.nextLine();
					String mensaje = ID + "_" + comando + "_ID" + commandId++;
					socket.send(mensaje);
					System.out.println("Mensaje enviado");
					MessageMatcher msgMatcher = new MessageMatcher(mensaje);
					if (msgMatcher.clientDisconnect() != null) {
						listenThread.keepRuning = false;
						keepRunning = false;
					}
				}
			} catch(IOException e) {
				e.printStackTrace(System.err);
				System.out.println(e.toString());
			} finally {
				input.close();
			}

			socket.close();
		} catch(SocketTimeoutException e) {
			System.out.println("Se supero el tiempo de espera.");
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());
		}

		System.out.println("Saliendo.");
	}
}

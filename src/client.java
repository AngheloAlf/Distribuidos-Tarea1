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

			Listen listenThread = new Listen(socket);
			listenThread.start();

			// Aqui deberiamos obtener el client ID
			String ID = "Client1";
			// Ya obtuvimos el ID desde el servidor

			String comando;
			Scanner input = new Scanner(System.in);
			while(listenThread.isAlive()){
				comando = input.nextLine();
				socket.send(comando);
				System.out.println("Mensaje enviado");
			}
			input.close();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());
		}

	}
}

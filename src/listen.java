import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

class Listen extends Thread
{
	// private static Integer clientId = null;
	volatile protected MulticastHelper socket;
	volatile public Boolean keepRuning = true;
	private static Pattern clientPattern = Pattern.compile("^Client([0-9]+)_.*$", Pattern.CASE_INSENSITIVE);

	Listen(MulticastHelper socket){
		this.socket = socket;
	}

	public void run() 
	{
		try {
			while(keepRuning){
				String message = socket.receive();
				if(!clientPattern.matcher(message).matches()) { // es un mensaje del servidor y no de algun cliente
					System.out.println(message);
				}
			}
		} catch(SocketTimeoutException e){
			System.out.println("Se supero el tiempo de espera.");
			System.out.println("Presione enter para salir.");
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		finally {
			socket.close();
		}

	}
}

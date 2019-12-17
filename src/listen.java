import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Listen extends Thread
{
	public String clientId = null;
	volatile protected MulticastHelper socket;
	volatile public Boolean keepRuning = true;
	private static Pattern clientPattern = Pattern.compile("^Client([0-9]+)_.*$", Pattern.CASE_INSENSITIVE);
	private static Pattern connectIdPattern = Pattern.compile("^CONNECT_CLIENT([0-9]+)$", Pattern.CASE_INSENSITIVE);

	Listen(MulticastHelper socket) throws IOException, SocketTimeoutException
	{
		this.socket = socket;
		this.connectAndGetId();
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
		} catch(SocketException e) {

		} catch(SocketTimeoutException e){
			System.out.println("Se supero el tiempo de espera.");
			System.out.println("Presione enter para salir.");
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.out.println(e.toString());
		}
		finally {
			socket.close();
		}

	}

	protected void connectAndGetId() throws IOException, SocketTimeoutException
	{
		socket.send("CONNECT");
		while(clientId == null){
			String message = socket.receive();
			Matcher matcher = connectIdPattern.matcher(message);
			if(matcher.matches()){
				clientId = matcher.group(1);
			}
		}
	}
}

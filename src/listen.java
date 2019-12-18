import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

class Listen extends Thread
{
	volatile public String clientId = null;
	volatile protected MulticastHelper socket;
	volatile public Boolean keepRuning = true;

	Listen(MulticastHelper socket) throws IOException, SocketTimeoutException
	{
		this.socket = socket;
		this.connectAndGetId();
	}

	public void run() 
	{
		try {
			while (keepRuning) {
				String message = socket.receive();
				MessageMatcher msgMatcher = new MessageMatcher(message);
				// TODO: cambiar a msgMatcher.isFromServer()
				if (!msgMatcher.isFromClient()) {
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
		while (clientId == null) {
			MessageMatcher msgMatcher = new MessageMatcher(socket.receive());
			CommandData cmdData = msgMatcher.serverConnectId();
			if (cmdData != null) {
				clientId = cmdData.getArgs()[0];
			}
		}
	}
}

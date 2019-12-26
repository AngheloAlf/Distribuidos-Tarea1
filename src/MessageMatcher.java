import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Analiza validez de mensajes y extrae los argumentos de cada comando.
class MessageMatcher
{
	// Mensajes especiales.
	// No tienen sintaxis de mensaje, solo contienen el comando.
	public static Pattern connectCommand = Pattern.compile("^(CONNECT)$", Pattern.CASE_INSENSITIVE);
	public static Pattern connectIdCommand = Pattern.compile("^(CONNECT_CLIENT)([0-9]+)$", Pattern.CASE_INSENSITIVE);

	// Mensajes del Cliente
	public static Pattern clientMessage = Pattern.compile("^CLIENT([0-9]+)_(.+)_ID([0-9]+)$", Pattern.CASE_INSENSITIVE);

	// Comandos del Cliente
	public static Pattern playCommand = Pattern.compile("^(PLAY)_(.+)_([0-9]+)$", Pattern.CASE_INSENSITIVE);
	public static Pattern stopCommand = Pattern.compile("^(STOP)$", Pattern.CASE_INSENSITIVE);
	public static Pattern pauseCommand = Pattern.compile("^(PAUSE)$", Pattern.CASE_INSENSITIVE);
	public static Pattern queueAddCommand = Pattern.compile("^(KEEWIH)_(.+)_([0-9]+)$", Pattern.CASE_INSENSITIVE);
	public static Pattern queueListCommand = Pattern.compile("^(LIST)$", Pattern.CASE_INSENSITIVE);
	public static Pattern nextCommand = Pattern.compile("^(NEXT)$", Pattern.CASE_INSENSITIVE);
	public static Pattern jumpCommand = Pattern.compile("^(JUMP)_(-?[0-9]+)$", Pattern.CASE_INSENSITIVE);
	public static Pattern historyCommand = Pattern.compile("^(HISTORY)$", Pattern.CASE_INSENSITIVE);
	public static Pattern disconnectCommand = Pattern.compile("^(EXIT)$", Pattern.CASE_INSENSITIVE);

	public static Pattern queueRawCommand = Pattern.compile("^(QUEUE)_(.+)_([0-9]+)$", Pattern.CASE_INSENSITIVE);

	protected String rawMessage;
	protected boolean isCommandValid = false;
	protected String command = "";

	protected boolean isMessageFromClient = false;
	protected String clientId = "";
	protected String clientMsgId = "";

	protected boolean isMessageFromServer = false;

	/// @param message: mensaje transmitido a la sesión Multicast.
	public MessageMatcher(String message)
	{
		this.rawMessage = message;

		Matcher clientMatcher = clientMessage.matcher(message);
		if (connectCommand.matcher(message).matches()) {
			this.isCommandValid = true;
			this.isMessageFromClient = true;

			this.command = message;
		} else if (connectIdCommand.matcher(message).matches()) {
			this.isCommandValid = true;
			this.isMessageFromServer = true;

			this.command = message;
		} else if (clientMatcher.matches()) {
			this.isMessageFromClient = true;

			this.clientId = clientMatcher.group(1);
			this.command = clientMatcher.group(2);
			this.clientMsgId = clientMatcher.group(3);
		}
	}

	/// Revisa la validez del comando.
	/// Un comando valido puede ser tanto del Cliente como del Servidor,
	/// pero debe calzar con alguno de los comandos existentes.
	public boolean isCommandValid()
	{
		if (isCommandValid) {
			return true;
		}

		if (isMessageFromClient) {
			if (playCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (stopCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (pauseCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (queueAddCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (queueListCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (nextCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (jumpCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (historyCommand.matcher(command).matches()) {
				isCommandValid = true;
			} else if (disconnectCommand.matcher(command).matches()) {
				isCommandValid = true;
			}
		}

		return isCommandValid;
	}

	/// Retorna ```true``` si la sintaxis calza con la sintaxis del Cliente.
	public boolean isFromClient()
	{
		return isMessageFromClient;
	}

	/// Retorna ```true``` si la sintaxis calza con la sintaxis del Servidor.
	public boolean isFromServer()
	{
		return isMessageFromServer;
	}

	/// Retorna el comando y sus argumentos, o ```null``` si no calza con el
	/// patron entregado.
	/// ```argsAmount``` indica la cantidad de argumentos que tiene el comando.
	private CommandData commandExtracter(Matcher commandMatcher, int argsAmount)
	{
		if (!commandMatcher.matches()) {
			return null;
		}

		String command = commandMatcher.group(1);
		String[] args = null;

		if (argsAmount > 0) {
			args = new String[argsAmount];

			for(int i = 0; i < argsAmount; ++i){
				args[i] = commandMatcher.group(i+2);
			}
		}

		return new CommandData(command, args);
	}

	public CommandData clientConnect()
	{
		return commandExtracter(connectCommand.matcher(command), 0);
	}
	public CommandData serverConnectId(){
		return commandExtracter(connectIdCommand.matcher(command), 1);
	}


	public CommandData clientPlay()
	{
		return commandExtracter(playCommand.matcher(command), 2);
	}
	public CommandData clientStop()
	{
		return commandExtracter(stopCommand.matcher(command), 0);
	}
	public CommandData clientPause()
	{
		return commandExtracter(pauseCommand.matcher(command), 0);
	}
	public CommandData clientQueueAdd()
	{
		return commandExtracter(queueAddCommand.matcher(command), 2);
	}
	public CommandData clientQueueList()
	{
		return commandExtracter(queueListCommand.matcher(command), 0);
	}
	public CommandData clientNext()
	{
		return commandExtracter(nextCommand.matcher(command), 0);
	}
	public CommandData clientJump()
	{
		return commandExtracter(jumpCommand.matcher(command), 1);
	}
	public CommandData clientHistory()
	{
		return commandExtracter(historyCommand.matcher(command), 0);
	}
	public CommandData clientDisconnect()
	{
		return commandExtracter(disconnectCommand.matcher(command), 0);
	}

}

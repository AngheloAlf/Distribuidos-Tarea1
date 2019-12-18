/// Contenedor para un comando y sus posibles argumentos.
class CommandData
{
	protected String command;
	protected String[] commandArgs;

	public CommandData(String command, String[] commandArgs)
	{
		this.command = command;
		this.commandArgs = commandArgs;
	}

	/// El comando.
	public String getCommand()
	{
		return command;
	}

	/// Entrega un arreglo con los argumentos del comando, o ```null```
	/// si la sintaxis del comando es sin argumento.
	public String[] getArgs()
	{
		return commandArgs;
	}
}

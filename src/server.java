class server
{
	public static void main(String args[])
	{
		if(args.length < 1) {
			System.out.println("ERROR: Debes especificar la IP!");
			System.out.println("Uso: java server <ip multicast>");
		}
		System.out.println("Hola Alf");
	}
}

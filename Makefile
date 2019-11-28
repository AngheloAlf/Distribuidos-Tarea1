default:
	javac -d . src/Cancion.java
	javac -d . src/listen.java
	javac -d . src/server.java
	javac -d . src/client.java
clean:
	rm -f Server.class
	rm -f Client.class
	rm -f Listen.class
	rm -f Cancion.class

default:
	javac -d . src/CommandData.java
	javac -d . src/MessageMatcher.java
	javac -d . src/MulticastHelper.java
	javac -d . src/Cancion.java
	javac -d . src/listen.java
	javac -d . src/server.java
	javac -d . src/client.java
clean:
	rm -f CommandData.class
	rm -f MessageMatcher.class
	rm -f MulticastHelper.class
	rm -f Server.class
	rm -f Client.class
	rm -f Listen.class
	rm -f Cancion.class

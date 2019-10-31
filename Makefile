default:
	javac -d . src/server.java
	javac -d . src/client.java
clean:
	rm server.class
	rm client.class

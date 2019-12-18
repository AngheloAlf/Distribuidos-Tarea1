default:
	javac -d . src/CommandData.java
	javac -d . src/MessageMatcher.java
	javac -d . src/MulticastHelper.java
	javac -d . src/Song.java
	javac -d . src/Singer.java
	javac -d . src/listen.java
	javac -d . src/server.java
	javac -d . src/client.java
clean:
	rm -f *.class

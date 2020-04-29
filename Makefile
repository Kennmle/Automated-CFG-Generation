all: cmd/Main.java
	javac -cp .:com.google.gson.jar cmd/*.java

run: cmd/Main.java
	java -cp .:com.google.gson.jar cmd.Main $(coverage) $(json_file)
	
graph-dep: graph/
	javac graph/*.java
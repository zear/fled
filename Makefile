JC	?= javac
TARGET	:= editor
SRC	:= Editor.java

$(TARGET): $(SRC)
	$(JC) $^

release:
	jar cfm $(TARGET).jar manifest *.class

clean:
	rm -f ./*.class ./$(TARGET).jar

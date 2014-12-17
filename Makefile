JC		?= javac
TARGET		:= editor
SRC_DIR		:= src
CLASS_DIR	:= class
SRC		:= $(SRC_DIR)/Editor.java

$(TARGET): $(SRC)
	$(JC) -cp ":$(SRC_DIR)" $^ -d $(CLASS_DIR)

release: $(TARGET)
	jar cfm $(TARGET).jar manifest -C $(CLASS_DIR) .

clean:
	rm -f $(CLASS_DIR)/*.class ./$(TARGET).jar

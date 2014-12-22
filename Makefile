JC		?= javac
TARGET		:= editor
SRC_DIR		:= src
CLASS_DIR	:= class
SRC		:= $(SRC_DIR)/gui/Editor.java

$(TARGET): $(SRC)
	$(JC) -cp ":$(SRC_DIR)" $^ -d $(CLASS_DIR)

release: $(TARGET)
	jar cfm $(TARGET).jar manifest -C $(CLASS_DIR) .

clean:
	rm -rf $(CLASS_DIR)/* ./$(TARGET).jar

PROGRAM= *.java c/*.java c/parse/*.java
CLASS_FILE= *.class c/*.class c/parse/*.class
all: $(PROGRAM) 
	javac $(PROGRAM) 

clean: $(PROGRAM)
	@rm $(CLASS_FILE)
	@echo "remove $(CLASS_FILE)"

run:
	@cd .. ; java lang.c.MiniCompiler lang/c/test.c

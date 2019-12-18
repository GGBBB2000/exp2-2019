.PHONY: all run clean token
all: *.class c/*.class c/parse/*.class

%.class: %.java
	javac *.java c/*.java c/parse/*.java

run: all
	@cd .. ; java lang.c.MiniCompiler lang/c/test.c

token:
	@cd .. ; java lang.c.TestCToken lang/c/test.c
clean:
	-rm -f *.class c/*.class c/parse/*.class

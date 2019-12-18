.PHONY: all run clean token
# all: *.class c/*.class c/parse/*.class
all: Assembler.class Compiler.class FatalErrorException.class IOContext.class LL1.class ParseContext.class ParseRule.class SimpleParseContext.class SimpleToken.class SimpleTokenizer.class SymbolTable.class SymbolTableEntry.class Token.class Tokenizer.class c/CParseContext.class c/CParseRule.class c/CToken.class c/CTokenRule.class c/CTokenizer.class c/CType.class c/MiniCompiler.class c/TestCToken.class c/TestCToken.class c/parse/Expression.class c/parse/ExpressionAdd.class c/parse/ExpressionSub.class c/parse/Factor.class c/parse/FactorAmp.class c/parse/Number.class c/parse/Program.class c/parse/Term.class
%.class: %.java
	javac *.java c/*.java c/parse/*.java

run: all
	@cd .. ; java lang.c.MiniCompiler lang/c/test.c

token:
	@cd .. ; java lang.c.TestCToken lang/c/test.c
clean:
	-rm -f *.class c/*.class c/parse/*.class

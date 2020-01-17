package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;

public class Array extends CParseRule {
    CParseRule expression;

    public Array(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LBRA;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        tokenizer.getNextToken(pcx);
        expression = new Expression(pcx);
        expression.parse(pcx);
        final var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_RBRA) {
            pcx.fatalError("arrayの[]が閉じていません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (expression != null) {
            System.out.print("Array[ ");
            expression.semanticCheck(pcx);
            final var cType = expression.getCType();
            if (cType.getType() != CType.T_int) {
                pcx.fatalError("配列のインデックスにはintしか使えません");
            }
            this.setCType(cType);
            this.setConstant(expression.isConstant());
            System.out.print("] ");
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; array starts");
        if (expression != null) {
            expression.codeGen(pcx);
        }
        o.println("\tMOV\t-(R6), R0\t; Array:配列が示している番地を計算し、格納します");
        o.println("\tADD\t-(R6), R0\t; Array:");
        o.println("\tMOV\tR0, (R6)+\t; Array:");
        o.println(";;; array completes");

    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;

public class PrimaryMult extends CParseRule {
    CParseRule variable;

    public PrimaryMult(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MUL;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);
        if (!Ident.isFirst(token)) {
            pcx.fatalError(
                    String.format("[%s]*(ポインタ)演算子の後ろはIdentifierです",
                            token.toExplainString()));
        }
        variable = new Variable(pcx);
        variable.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (variable != null) {
            variable.semanticCheck(pcx);
            final var type = variable.getCType().getType();
            if (type == CType.T_int) {
                pcx.fatalError("数値はデリファレンスできません");
            } else if (type == CType.T_pint) {
                this.setCType(CType.getCType(CType.T_int));
            }
            this.setConstant(variable.isConstant());
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; primarymult starts");
        if (variable != null) {
            variable.codeGen(pcx);
        }
        o.println("\tMOV\t-(R6), R0\t; PrimaryMult:番地から値を取り出す");
        o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
        o.println(";;; primarymult completes");
    }
}


package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class MinusFactor extends CParseRule {
    private CParseRule unsignedFactor;
    private CToken op;

    public MinusFactor(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        CToken tk = ct.getNextToken(pcx);

        unsignedFactor = new UnsignedFactor(pcx);
        unsignedFactor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        System.out.print("MINUS ");
        if (unsignedFactor != null) {
            unsignedFactor.semanticCheck(pcx);
            this.setCType(unsignedFactor.getCType());        // unsignedFactor の型をそのままコピー
            this.setConstant(unsignedFactor.isConstant());
            if (unsignedFactor.getCType().getType() == CType.T_pint) {
                pcx.fatalError("ポインタにーは付けられません");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; minusFactor starts");
        if (unsignedFactor != null) {
            unsignedFactor.codeGen(pcx);
        }
        o.println("\tMOV\t#0, R0\t; MinusFactor:");
        o.println("\tSUB\t-(R6), R0\t; MinusFactor:");
        o.println("\tMOV\tR0, (R6)+\t; MinusFactor:");
        o.println(";;; minusFactor completes");
    }
}

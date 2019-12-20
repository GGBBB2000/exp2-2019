
package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class MinusFactor extends CParseRule {
    private CParseRule unsignedFactor;
    public MinusFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        unsignedFactor = new UnsignedFactor(pcx);
        unsignedFactor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (unsignedFactor != null) {
            unsignedFactor.semanticCheck(pcx);
            this.setCType(unsignedFactor.getCType());		// unsignedFactor の型をそのままコピー
            this.setConstant(unsignedFactor.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; minusFactor starts");
        if (unsignedFactor != null) { unsignedFactor.codeGen(pcx); }
        o.println(";;; minusFactor completes");
    }
}

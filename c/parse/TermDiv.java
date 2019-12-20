package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends CParseRule {
    private CParseRule factor;
    public TermDiv(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_DIV;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        factor = new Factor(pcx);
        factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            this.setCType(factor.getCType());		// factor の型をそのままコピー
            this.setConstant(factor.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; termDivstarts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; termDivcompletes");
    }
}

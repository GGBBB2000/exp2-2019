package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
    // term ::= factor
    private CParseRule factor;
    public Term(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MUL;
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
        o.println(";;; termMult starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; termMult completes");
    }
}

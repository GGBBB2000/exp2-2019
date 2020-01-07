package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
    // term ::= factor
    private CParseRule factor;
    public TermMult(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MUL;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        factor = new Factor(pcx);
        factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        System.out.print("MULT Factor(");
        if (factor != null) {
            factor.semanticCheck(pcx);
            var type = factor.getCType().getType();
            if (type == CType.T_pint) {
                pcx.fatalError("乗算にポインタを用いることができません");
            }
            this.setCType(factor.getCType());		// factor の型をそのままコピー
            this.setConstant(factor.isConstant());
        }
        System.out.print(")");
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; termMult starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; termMult completes");
    }
}

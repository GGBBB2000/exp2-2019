package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
    // factor ::= factorAmp | number
    private CToken op;
    private CParseRule factor;
    public Factor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        //return Number.isFirst(tk);
        return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        var ct = pcx.getTokenizer();
        var token = ct.getCurrentToken(pcx);
        switch (token.getType()) {
            case CToken.TK_PLUS:
                factor = new PlusFactor(pcx);
                break;
            case CToken.TK_MINUS:
                factor = new MinusFactor(pcx);
                break;
            default:
                factor = new UnsignedFactor(pcx);
                break;
        }
        factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        System.out.print("Factor(");
        if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());		// numberの型をそのままコピー
            setConstant(factor.isConstant());	// factor は常に定数
        }
        System.out.print(")");
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factor starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; factor completes");
    }
}

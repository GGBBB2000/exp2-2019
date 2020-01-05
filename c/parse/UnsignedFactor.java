package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
    // factor ::= factorAmp | number
    private CToken op;
    private CParseRule factor;
    private boolean hasPar = false;
    public UnsignedFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        //return Number.isFirst(tk);

        return Number.isFirst(tk) || FactorAmp.isFirst(tk) || tk.getType() == CToken.TK_LPAR;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        var ct = pcx.getTokenizer();
        var token = ct.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_AMP) {
            factor= new FactorAmp(pcx);
        } else if (token.getType() == CToken.TK_LPAR) {
            token = ct.getNextToken(pcx);
            hasPar = true;
            if (Expression.isFirst(token)) {
                factor = new Expression(pcx);
                factor.parse(pcx);
                token = ct.getCurrentToken(pcx);
                if (token.getType() != CToken.TK_RPAR) {
                    pcx.fatalError(token.toExplainString() + "()が閉じていません.");
                }
                token = ct.getCurrentToken(pcx);
            } else {
                pcx.fatalError(token.toExplainString() + "TK_LPARの後ろはExpressionです");
            }
        } else {
            factor = new Number(pcx);
        }
        factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());		// numberの型をそのままコピー
            setConstant(factor.isConstant());	// factor は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factor starts");
        if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; factor completes");
    }
}

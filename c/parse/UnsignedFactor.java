package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class UnsignedFactor extends CParseRule {
    // factor ::= factorAmp | number
    private CToken op;
    private CParseRule unsignedFactor;
    private boolean hasPar = false;

    public UnsignedFactor(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        //return Number.isFirst(tk);

        return Number.isFirst(tk)
                || FactorAmp.isFirst(tk)
                || tk.getType() == CToken.TK_LPAR
                || AddressToValue.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        var ct = pcx.getTokenizer();
        var token = ct.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_AMP) {
            unsignedFactor = new FactorAmp(pcx);
            unsignedFactor.parse(pcx);
        } else if (token.getType() == CToken.TK_LPAR) {
            token = ct.getNextToken(pcx);
            hasPar = true;
            if (Expression.isFirst(token)) {
                unsignedFactor = new Expression(pcx);
                unsignedFactor.parse(pcx);
                token = ct.getCurrentToken(pcx);
                if (token.getType() != CToken.TK_RPAR) {
                    pcx.fatalError(token.toExplainString() + "()が閉じていません.");
                }
                token = ct.getNextToken(pcx);
            } else {
                pcx.fatalError(token.toExplainString() + "TK_LPARの後ろはExpressionです");
            }
        } else {
            unsignedFactor = new Number(pcx);
            unsignedFactor.parse(pcx);
        }
        // factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        System.out.print("UnsignedFactor( ");
        if (hasPar) {
            System.out.print("LPAR ");
        }
        if (unsignedFactor != null) {
            unsignedFactor.semanticCheck(pcx);
            setCType(unsignedFactor.getCType());        // number factorAmp expressionの型をそのままコピー
            setConstant(unsignedFactor.isConstant());    // factor は常に定数
        }
        if (hasPar) {
            System.out.print("RPAR ");
        }
        System.out.print(")");
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; unsignedFactor starts");
        if (unsignedFactor != null) {
            unsignedFactor.codeGen(pcx);
        }
        o.println(";;; unsignedFactor completes");
    }
}

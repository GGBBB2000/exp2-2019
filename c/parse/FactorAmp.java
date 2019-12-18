package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
    // factor ::= number
    private CToken op;
    private CParseRule number;
    public FactorAmp(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
            return tk.getType() == CToken.TK_AMP;//&が最初か
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        // &の次の字句を読む
        CToken tk = ct.getNextToken(pcx);
        
        if (Number.isFirst(tk)) {
            number = new Number(pcx);
            number.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "&の後ろはNumberです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (number != null) {
            number.semanticCheck(pcx);
            setCType(number.getCType());		// number の型をそのままコピー
            setConstant(number.isConstant());	// number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}

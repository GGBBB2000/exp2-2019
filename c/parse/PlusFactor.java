
package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.io.PrintStream;

public class PlusFactor extends CParseRule {
    private CParseRule unsignedFactor;
    private CToken op;
    public PlusFactor(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_PLUS;
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
        if (unsignedFactor != null) {
            unsignedFactor.semanticCheck(pcx);
            this.setCType(unsignedFactor.getCType());		// unsignedFactor の型をそのままコピー
            this.setConstant(unsignedFactor.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; plusFactor starts");
        if (unsignedFactor != null) { unsignedFactor.codeGen(pcx); }
        o.println(";;; plusFactor completes");
    }
}

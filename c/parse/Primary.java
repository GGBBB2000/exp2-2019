package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Primary extends CParseRule {
    CParseRule multVariable;
    public boolean hasMultPrimary = false;
    public Primary(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return PrimaryMult.isFirst(tk) | Variable.isFirst(tk);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_MUL) {
            multVariable = new PrimaryMult(pcx);
            hasMultPrimary = true;
        } else {
            multVariable = new Variable(pcx);
            hasMultPrimary = false;
        }
        multVariable.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (multVariable != null) {
            System.out.print("Primary( ");
            multVariable.semanticCheck(pcx);
            this.setCType(multVariable.getCType());
            this.setConstant(false);
            System.out.print(")");
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

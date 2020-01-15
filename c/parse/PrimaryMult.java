package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class PrimaryMult extends CParseRule {
    CParseRule variable;

    public PrimaryMult(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MUL;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

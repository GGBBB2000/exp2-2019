package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Primary extends CParseRule {
    CParseRule multVariable;

    public Primary(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return PrimaryMult.isFirst(tk) | Variable.isFirst(tk);
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

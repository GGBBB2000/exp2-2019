package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class StatementIf extends CParseRule {
    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_IF;
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

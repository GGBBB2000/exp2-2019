package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Declaration extends CParseRule {

    public static boolean isFirst(CToken token) {
        return IntDecl.isFirst(token) || ConstDecl.isFirst(token);
    }

    ;

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

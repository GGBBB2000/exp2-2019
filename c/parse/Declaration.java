package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Declaration extends CParseRule {
    CParseRule decl;
    public static boolean isFirst(CToken token) {
        return IntDecl.isFirst(token) || ConstDecl.isFirst(token);
    }

    public Declaration(CParseContext pcx) {
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_INT) {
            decl = new IntDecl(pcx);
        } else if (token.getType() == CToken.TK_CONST) {
            decl = new ConstDecl(pcx);
        } else {
            pcx.fatalError(token.toExplainString() + "int/constが予測されます");
        }
        decl.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (decl != null) {
            decl.semanticCheck(pcx);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        if (decl != null) {
            var o = pcx.getIOContext().getOutStream();
            o.println(";;; Declaration starts");
            decl.codeGen(pcx);
            o.println(";;; Declaration completes");
        }
    }
}

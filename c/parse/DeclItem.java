package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class DeclItem extends CParseRule {

    CParseRule ident, num;
    private boolean hasMul = false;

    public DeclItem(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        final var tokenType = token.getType();
        return (tokenType == CToken.TK_MUL) || (tokenType == CToken.TK_IDENT);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_MUL) {
            hasMul = true;
            token = tokenizer.getNextToken(pcx);
        }
        if (token.getType() == CToken.TK_IDENT) {
            ident = new Ident(pcx);
        } else {
            pcx.fatalError(token.toExplainString() + "識別子が有りません");
        }
        ident.parse(pcx);
        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_LBRA) {
            num = new Number(pcx);
            num.parse(pcx);
            token = tokenizer.getNextToken(pcx);
            if (token.getType() != CToken.TK_RBRA) {
                pcx.fatalError("[]が閉じていません");
            }
            tokenizer.getNextToken(pcx);
        }
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

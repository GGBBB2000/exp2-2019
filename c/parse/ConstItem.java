package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class ConstItem extends CParseRule {
    private boolean hasMul = false;
    private boolean hasAmp = false;
    private CParseRule ident;
    private CParseRule num;

    public ConstItem(CParseContext pcx) {

    }


    public static boolean isFirst(CToken token) {
        final var tokenType = token.getType();
        return (tokenType == CToken.TK_MUL) || (tokenType == CToken.TK_IDENT);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        var tokenizer = pcx.getTokenizer();
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
        if (token.getType() != CToken.TK_ASSIGN) {
            pcx.fatalError(token.toExplainString() + "=が予測されます");
        }
        token = tokenizer.getNextToken(pcx);
        if (token.getType() == CToken.TK_AMP) {
            hasAmp = true;
            tokenizer.getNextToken(pcx);
        }
        num = new Number(pcx);
        num.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

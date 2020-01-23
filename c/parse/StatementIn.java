package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class StatementIn extends CParseRule {
    private CParseRule expression;
    public StatementIn(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_INPUT;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        tokenizer.getNextToken(pcx);
        expression = new Expression(pcx);
        expression.parse(pcx);
        var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (expression != null) {
            expression.semanticCheck(pcx);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

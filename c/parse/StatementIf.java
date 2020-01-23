package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class StatementIf extends CParseRule {
    CParseRule ifStatement, condition, elseStatement;
    public StatementIf(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_IF;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx); //IFの次を読む

        if (token.getType() != CToken.TK_LPAR) {
            pcx.fatalError(token.toExplainString() + "予期せぬトークンです");
        }
        tokenizer.getNextToken(pcx);
        condition = new ConditionExpression(pcx);
        condition.parse(pcx);
        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_RPAR) {
            pcx.fatalError(token.toExplainString() + "()が閉じていません");
        }
        tokenizer.getNextToken(pcx);
        ifStatement = new Statement(pcx);
        ifStatement.parse(pcx);
        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() == CToken.TK_ELSE) {
            tokenizer.getNextToken(pcx);
            elseStatement = new Statement(pcx);
            elseStatement.parse(pcx);
        }
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

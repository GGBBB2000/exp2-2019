package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class StatementDoWhile extends CParseRule {
    private CParseRule statement;
    private CParseRule condition;
    public StatementDoWhile(CParseContext pcx) {

    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_DO;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx); // doの次を読む
        statement = new Statement(pcx);
        statement.parse(pcx);
        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_WHILE) {
            pcx.fatalError(token.toExplainString() +"Whileキーワードが見つかりません");
        }
        token = tokenizer.getNextToken(pcx);
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
        token = tokenizer.getNextToken(pcx);
        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

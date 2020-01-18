package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class StatementAssign extends CParseRule {
    CParseRule primary, expression;

    public StatementAssign(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        primary = new Primary(pcx);
        primary.parse(pcx);

        var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_ASSIGN) {
            pcx.fatalError(token.toExplainString() + "primaryの後に=が必要です");
        }
        tokenizer.getNextToken(pcx);
        expression = new Expression(pcx);
        expression.parse(pcx);
        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
        }
        if (expression != null) {
            expression.semanticCheck(pcx);
        }
        final var primaryType = primary.getCType();
        final var expressionType = expression.getCType();
        final var primaryIsConstant = primary.isConstant();
        if (primaryType.getType() != expressionType.getType()) {
            pcx.fatalError(String.format("左辺の型[%s]と右辺の型[%s]が一致しません\n",
                    primaryType.toString(), expressionType.toString()));
        }
        if (primaryIsConstant) {
            pcx.fatalError("定数に値を代入することはできません");
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

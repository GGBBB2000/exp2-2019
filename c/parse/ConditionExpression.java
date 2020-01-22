package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class ConditionExpression extends CParseRule {
    private CParseRule condition;

    public ConditionExpression(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return Expression.isFirst(token)
                || token.getType() == CToken.TK_TRUE
                || token.getType() == CToken.TK_FALSE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        var expression = new Expression(pcx);
        expression.parse(pcx);

        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);
        switch (token.getType()) {
            case CToken.TK_EQ:
                condition = new ConditionEQ(expression);
                break;
            case CToken.TK_GE:
                condition = new ConditionGE(expression);
                break;
            case CToken.TK_GT:
                condition = new ConditionGT(expression);
                break;
            case CToken.TK_LE:
                condition = new ConditionLE(expression);
                break;
            case CToken.TK_LT:
                condition = new ConditionLT(expression);
                break;
            case CToken.TK_NE:
                condition = new ConditionNE(expression);
                break;
            default:
                pcx.fatalError(token.toExplainString() + "比較演算子がありません");
                break;
        }
        condition.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionLT extends CParseRule {
    private CParseRule left;

    public ConditionLT(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_LT;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionLE extends CParseRule {
    private CParseRule left;

    public ConditionLE(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_LE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionGT extends CParseRule {
    private CParseRule left;

    public ConditionGT(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_GT;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionGE extends CParseRule {
    private CParseRule left;

    public ConditionGE(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_GE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionEQ extends CParseRule {
    private CParseRule left;

    public ConditionEQ(CParseRule expression) {
        this.left = expression;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_EQ;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionNE extends CParseRule {
    private CParseRule left;

    public ConditionNE(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_NE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);

        if (!Expression.isFirst(token)) {
            pcx.fatalError(token.toExplainString() + "比較演算子の後に式がありません");
        }
        left = new Expression(pcx);
        left.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}
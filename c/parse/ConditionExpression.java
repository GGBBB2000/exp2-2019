package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.util.Optional;

public class ConditionExpression extends CParseRule {
    private CParseRule condition;
    private Optional<Boolean> status = Optional.ofNullable(null);
    public ConditionExpression(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return Expression.isFirst(token)
                || token.getType() == CToken.TK_TRUE
                || token.getType() == CToken.TK_FALSE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_TRUE && token.getType() != CToken.TK_FALSE) {
            var expression = new Expression(pcx);
            expression.parse(pcx);

            token = tokenizer.getCurrentToken(pcx);
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
        } else {
            if (token.getType() == CToken.TK_TRUE) {
                status = Optional.of(true);
            } else {
                status = Optional.of(false);
            }
        }
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.semanticCheck(pcx);
            this.setCType(condition.getCType());
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionLT extends CParseRule {
    private CParseRule left;
    private CParseRule right;

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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionLE extends CParseRule {
    private CParseRule left;
    private CParseRule right;

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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionGT extends CParseRule {
    private CParseRule left;
    private CParseRule right;

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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionGE extends CParseRule {
    private CParseRule left;
    private CParseRule right;

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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionEQ extends CParseRule {
    private CParseRule left;
    private CParseRule right;

    public ConditionEQ(CParseRule left) {
        this.left = left;
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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

class ConditionNE extends CParseRule {
    private CParseRule left;
    private CParseRule right;

    public ConditionNE(CParseRule left) {
        this.left = left;
        ;
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
        right = new Expression(pcx);
        right.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            final var leftType = left.getCType();
            final var rightType = right.getCType();
            if (leftType.getType() != rightType.getType()) {
                final var format = String.format("比較式の両辺の型([%s]と[%s]が一致しません"
                        , leftType.toString(), rightType.toString());
                pcx.fatalError(format);
            }
        }
        this.setCType(CType.getCType(CType.T_bool));
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}
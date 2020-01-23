package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;
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
            tokenizer.getNextToken(pcx);
        }
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.semanticCheck(pcx);
            this.setCType(condition.getCType());
            this.setConstant(condition.isConstant());
            assert condition.isConstant() : "conditionは絶対に定数";
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition starts");
        if (condition != null) {
            condition.codeGen(pcx);
        }
        status.ifPresent(IsTrue -> {
            if (IsTrue) {
                o.println("\\tMOV\\t#0x0001, (R6)+\\t; Condition: true(1)を積む");
            } else {
                o.println("\tMOV\t#0x0000, (R6)+\t; Condition: false(0)を積む");
            }
        });
        o.println(";;; condition completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition < (compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionLT: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionLT:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
            o.println("\tCMP\tR0, R1\t; ConditionLT: R1 < R0 = R1-R0 < 0");
            o.println("\tBRN\tLT" + seq + " ; ConditionLT:");
            o.println("\tCLR\tR2\t\t; ConditionLT: set false");
            o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT:");
        }
        o.println(";;; condition < (compare) completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition <= (compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionLE: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionLE:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionLE: set true");
            o.println("\tCMP\tR0, R1\t; ConditionLE: R1 <= R0 = R1-R0 <= 0");
            o.println("\tBRZ\tLE" + seq + " ; ConditionLE:");
            o.println("\tBRN\tLE" + seq + " ; ConditionLE:");
            o.println("\tCLR\tR2\t\t; ConditionLE: set false");
            o.println("LE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLE:");
        }
        o.println(";;; condition <= (compare) completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition >(compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionGT: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionGT:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionGT: set true");
            o.println("\tCMP\tR1, R0\t; ConditionGT: R1 > R0 = R0 - R1 < 0");
            o.println("\tBRN\tGT" + seq + " ; ConditionGT:");
            o.println("\tCLR\tR2\t\t; ConditionGT: set false");
            o.println("GT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGT:");
        }
        o.println(";;; condition > (compare) completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition >= (compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionGE: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionGE:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionGE: set true");
            o.println("\tCMP\tR1, R0\t; ConditionGE: (R1 >= R0) = (R0 - R1 =< 0)");
            o.println("\tBRZ\tGE" + seq + " ; ConditionGE:");
            o.println("\tBRN\tGE" + seq + " ; ConditionGE:");
            o.println("\tCLR\tR2\t\t; ConditionGE: set false");
            o.println("GE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGE:");
        }
        o.println(";;; condition >= (compare) completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition ==(compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionEQ: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionEQ:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionEQ: set true");
            o.println("\tCMP\tR1, R0\t; ConditionEQ: R1 == R0 = R0 - R1 = 0");
            o.println("\tBRZ\tEQ" + seq + " ; ConditionEQ:");
            o.println("\tCLR\tR2\t\t; ConditionEQ: set false");
            o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; ConditionEQ:");
        }
        o.println(";;; condition == (compare) completes");
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
        this.setConstant(true);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition ==(compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; ConditionNE: 2数を取り出して, 比べる");
            o.println("\tMOV\t-(R6), R1\t; ConditionNE:");
            o.println("\tCLR\tR2\t\t; ConditionNE: set false");
            o.println("\tCMP\tR1, R0\t; ConditionNE: R1 == R0 = R0 - R1 = 0");
            o.println("\tBRZ\tNE" + seq + " ; ConditionNE:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionNE: set true");
            o.println("NE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionNE:");
        }
        o.println(";;; condition == (compare) completes");
    }
}
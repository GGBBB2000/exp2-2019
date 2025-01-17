package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class StatementWhile extends CParseRule {
    CParseRule condition, statement;
    public StatementWhile(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_WHILE;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx);

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
        statement = new Statement(pcx);
        statement.parse(pcx);

    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.semanticCheck(pcx);
        }
        if (statement != null) {
            statement.semanticCheck(pcx);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        final var seq = pcx.getSeqId();
        o.println(";;; StatementWhile Starts");
        o.println("while" + seq + ":\t;StatementWhile: ラベル生成");
        if (condition != null) {
            condition.codeGen(pcx);
        }
        o.println("\tMOV\t-(R6), R0\t;StatementWhile: スタックからconditionの結果を持ってくる");
        o.println("BRZ whileEnd" + seq + "\t;;; StatementWhile:");
        if (statement != null) {
            statement.codeGen(pcx);
        }
        o.println("\tJMP while" + seq + ":\t;StatementWhile:");
        o.println("whileEnd" + seq + ":\t;StatementWhile: ラベル生成");
        o.println(";;; StatementWhile Completes");
    }
}

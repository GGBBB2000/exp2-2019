package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

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
        o.println(";;; StatementDoWhile Starts");
        o.println("doWhile" + seq + ": \t;StatementDoWhile: ラベル生成");
        if (statement != null) {
            statement.codeGen(pcx);
        }
        if (condition != null) {
            condition.codeGen(pcx);
        }
        o.println("\tMOV\t-(R6), R0\t;StatementDoWhile: スタックからconditionの結果を持ってくる");
        o.println("\nBRZ doWhileEnd" + seq + "\t;;; StatementDoWhile Completes");
        o.println("\nJMP doWhile" + seq + "\t;;; StatementDoWhile Completes");
        o.println("doWhileEnd" + seq + ":\t;StatementDoWhile: ラベル生成");
        o.println(";;; StatementDoWhile Completes");
    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

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
        if (condition != null) {
            condition.semanticCheck(pcx);
        }
        if (ifStatement != null) {
            ifStatement.semanticCheck(pcx);
        }
        if (elseStatement != null) {
            elseStatement.semanticCheck(pcx);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        final var seq = pcx.getSeqId();
        o.println(";;; StatementsIf Starts");
        if (condition != null) {
            condition.codeGen(pcx);
        }
        o.println("\tMOV\t-(R6), R0\t;StatementIF: スタックからconditionの結果を持ってくる");
        o.println("\tBRZ endIf" + seq + "\t;;; StatementIF:");
        if (ifStatement != null) {
            ifStatement.codeGen(pcx);
        }
        if (elseStatement != null) {
            o.println("\tJMP endElse" + seq + "\t;;; StatementIF:");
            o.println("endIf" + seq + ": \t\t;StatementIF: ラベル生成");
            elseStatement.codeGen(pcx);
            o.println("endElse" + seq + ": \t\t;StatementIF: ラベル生成");
        } else {
            o.println("endIf" + seq + ": \t\t;StatementIF: ラベル生成");
        }
        o.println(";;; StatementsIf Completes");
    }
}

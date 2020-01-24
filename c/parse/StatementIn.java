package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class StatementIn extends CParseRule {
    private CParseRule primary;
    public StatementIn(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_INPUT;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        tokenizer.getNextToken(pcx);
        primary = new Primary(pcx);
        primary.parse(pcx);
        var token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            if (primary.isConstant()) {
                pcx.fatalError("定数に値を入力することはできません");
            }
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (primary != null) {
            primary.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0; StatementIn: 変数のアドレスをスタックからpop");
            o.println("\tMOV\t#0xFFE0, R3\t; StatementIn:");
            o.println("\tMOV\t(R3), (R0)\t; StatementIn: Primaryの値を変数に書き込み");
        }
    }
}

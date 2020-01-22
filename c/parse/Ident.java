package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;

public class Ident extends CParseRule {
    CToken identifier;

    public Ident(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IDENT;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getCurrentToken(pcx);
        final var table = pcx.getTable();
        if (token.getType() != CToken.TK_IDENT) {
            System.out.println("\n" + token.getType());
            pcx.fatalError("識別子がありません");
        }
        if (table.globalSearch(token.getText()) == null) {
            pcx.fatalError(token.toExplainString() + "未定義の変数です");
        }
        identifier = token;
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (identifier != null) {
            final var table = pcx.getTable();
            final var entry = table.globalSearch(identifier.getText());

            this.setCType(entry.getType());
            this.setConstant(entry.isConstant());
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ident starts");
        if (identifier != null) {
            o.println("\tMOV\t#" + identifier.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
                    + identifier.toExplainString() + ">");
        }
        o.println(";;; ident completes");
    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class Statement extends CParseRule {
    CParseRule statement;

    public Statement(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk)
                || StatementBlock.isFirst(tk)
                || StatementDoWhile.isFirst(tk)
                || StatementIf.isFirst(tk)
                || StatementIn.isFirst(tk)
                || StatementOut.isFirst(tk)
                || StatementWhile.isFirst(tk);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getCurrentToken(pcx);
        switch(token.getType()) {
            case CToken.TK_OUTPUT:
                statement = new StatementOut(pcx);
                break;
            case CToken.TK_INPUT:
                statement = new StatementIn(pcx);
                break;
            case CToken.TK_LCUR:
                statement = new StatementBlock(pcx);
                break;
            case CToken.TK_DO:
                statement = new StatementDoWhile(pcx);
                break;
            case CToken.TK_WHILE:
                statement = new StatementWhile(pcx);
                break;
            case CToken.TK_IF:
                statement = new StatementIf(pcx);
                break;
            default:
                statement = new StatementAssign(pcx);
                break;
        }
        statement.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (statement != null) {
            statement.semanticCheck(pcx);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        final var printStream = pcx.getIOContext().getOutStream();
        printStream.println(";;; Statement starts");
        if (statement != null) {
            statement.codeGen(pcx);
        }
        printStream.println(";;; Statement completes");
    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class ConstItem extends CParseRule {
    private boolean hasMul = false;
    private boolean hasAmp = false;
    private CToken ident;
    private CToken num;

    public ConstItem(CParseContext pcx) {

    }


    public static boolean isFirst(CToken token) {
        final var tokenType = token.getType();
        return (tokenType == CToken.TK_MUL) || (tokenType == CToken.TK_IDENT);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        var table = pcx.getTable();
        var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);

        if (token.getType() == CToken.TK_MUL) {
            hasMul = true;
            token = tokenizer.getNextToken(pcx);
        }

        if (token.getType() == CToken.TK_IDENT) {
            ident = token;
        } else {
            pcx.fatalError(token.toExplainString() + "識別子が有りません");
        }

        final var tokenText = token.getText();
        if (table.globalSearch(tokenText) != null) {
            pcx.fatalError(tokenText + "は既に定義されています");
        }

        token = tokenizer.getNextToken(pcx);
        if (token.getType() != CToken.TK_ASSIGN) {
            pcx.fatalError(token.toExplainString() + "=が予測されます");
        }
        token = tokenizer.getNextToken(pcx);
        if (token.getType() == CToken.TK_AMP) {
            hasAmp = true;
            token = tokenizer.getNextToken(pcx);
        }
        if (token.getType() != CToken.TK_NUM) {
            pcx.fatalError(token.toExplainString() + "=の後には数値が必要です");
        }
        num = token;
        tokenizer.getNextToken(pcx);

        var cType = hasMul ? CType.getCType(CType.T_pint) : CType.getCType(CType.T_int);
        final var entry = new CSymbolTableEntry(cType, 1, true, true, 0);
        table.setGlobalEntry(tokenText, entry);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (ident != null) {
            o.println(";;; constItem starts");
            final var label = ident.getText();
            final var value = num.getIntValue();
            if (num != null) {
                o.printf("%s: .WORD %d\n", label, value);
            }
            o.println(";;; constItem completes");
        }
    }
}

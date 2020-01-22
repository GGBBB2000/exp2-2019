package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class DeclItem extends CParseRule {

    CToken ident, num;
    private boolean hasMul = false;

    public DeclItem(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        final var tokenType = token.getType();
        return (tokenType == CToken.TK_MUL) || (tokenType == CToken.TK_IDENT);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);
        var table = pcx.getTable();
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
        token = tokenizer.getNextToken(pcx);

        var cType = hasMul ? CType.getCType(CType.T_pint) : CType.getCType(CType.T_int);
        var variableSize = 1;
        if (token.getType() == CToken.TK_LBRA) {
            token = tokenizer.getNextToken(pcx);
            if (token.getType() != CToken.TK_NUM) {
                pcx.fatalError(token.toExplainString() + "Numberがありません");
            }
            num = token;
            variableSize = token.getIntValue();
            token = tokenizer.getNextToken(pcx);
            if (token.getType() != CToken.TK_RBRA) {
                pcx.fatalError("[]が閉じていません");
            }
            tokenizer.getNextToken(pcx);
            if (cType.getType() == CType.T_pint) {
                cType = CType.getCType(CType.T_pint_arr);
            } else {
                cType = CType.getCType(CType.T_int_arr);
            }
        }
        if (table.globalSearch(tokenText) != null) {
            pcx.fatalError(tokenText + "は既に定義されています");
        }
        final var entry = new CSymbolTableEntry(cType, variableSize, false, true, 0);
        table.setGlobalEntry(tokenText, entry);
        table.printGlobal();
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (ident != null) {
            o.println(";;; DeclItem starts");
            final var label = ident.getText();
            if (num != null) {
                final var size = num.getIntValue();
                o.printf("%s: .BLKW %d\n", label, size);
            } else {
                o.printf("%s: .WORD %d\n", label, 0);
            }
            o.println(";;; DeclItem completes");
        }
    }
}

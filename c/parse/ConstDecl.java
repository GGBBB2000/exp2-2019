package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.io.PrintStream;
import java.util.ArrayList;

public class ConstDecl extends CParseRule {
    ArrayList<CParseRule> constItem = new ArrayList<>();

    public ConstDecl(CParseContext pcx) {
    }

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_CONST;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx);
        if (token.getType() != CToken.TK_INT) {
            pcx.fatalError(token.toExplainString() + "型宣言が必要です");
        }
        tokenizer.getNextToken(pcx);
        do {
            CParseRule tmp_decl = new ConstItem(pcx);
            tmp_decl.parse(pcx);
            constItem.add(tmp_decl);
            token = tokenizer.getCurrentToken(pcx);
            if (token.getType() == CToken.TK_COMMA) {
                token = tokenizer.getNextToken(pcx);
            } else if (DeclItem.isFirst(token)) {
                pcx.fatalError(token.toExplainString() + "予期せぬトークン\n\",\"が期待されます");
            }
        } while (ConstItem.isFirst(token));

        token = tokenizer.getCurrentToken(pcx);
        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        constItem.forEach(s -> {
            try {
                s.semanticCheck(pcx);
            } catch (FatalErrorException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        constItem.stream()
                .forEach(item -> {
                    o.println(";;; constDecl starts");
                    try {
                        item.codeGen(pcx);
                    } catch (FatalErrorException e) {
                        e.printStackTrace();
                    }
                    o.println(";;; constDecl completes");
                });
    }
}

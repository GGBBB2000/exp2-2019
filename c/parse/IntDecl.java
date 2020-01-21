package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.util.ArrayList;

public class IntDecl extends CParseRule {
    ArrayList<CParseRule> declItem = new ArrayList<>();
    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_INT;
    }

    public IntDecl(CParseContext pcx) {
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx);
        do {
            CParseRule tmp_decl = new DeclItem(pcx);
            tmp_decl.parse(pcx);
            declItem.add(tmp_decl);
            token = tokenizer.getCurrentToken(pcx);

            if (token.getType() == CToken.TK_COMMA) {
                token = tokenizer.getNextToken(pcx);
            } else if (DeclItem.isFirst(token)) {
                pcx.fatalError(token.toExplainString() + "予期せぬトークン\n\",\"が期待されます");
            }
        } while (DeclItem.isFirst(token));

        if (token.getType() != CToken.TK_SEMI) {
            pcx.fatalError(token.toExplainString() + ";がありません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

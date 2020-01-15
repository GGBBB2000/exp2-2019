package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class PrimaryMult extends CParseRule {
    CParseRule variable;

    public PrimaryMult(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MUL;
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        final var token = tokenizer.getNextToken(pcx);
        if (!Ident.isFirst(token)) {
            pcx.fatalError(
                    String.format("[%s]*(ポインタ)演算子の後ろはIdentifierです",
                            token.toExplainString()));
        }
        variable = new Variable(pcx);
        variable.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

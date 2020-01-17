package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

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
        if (token.getType() != CToken.TK_IDENT) {
            pcx.fatalError("識別子がありません");
        }
        identifier = token;
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (identifier != null) {
            System.out.print("IDent ");
            this.setCType(CType.getCType(CType.T_pint_arr));
            this.setConstant(false);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

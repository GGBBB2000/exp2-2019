package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.util.Optional;

public class Variable extends CParseRule {
    CParseRule ident;
    Optional<CParseRule> array;

    public Variable(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Ident.isFirst(tk);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        ident = new Ident(pcx);
        ident.parse(pcx);
        var token = tokenizer.getCurrentToken(pcx);
        CParseRule expression = null;
        if (token.getType() == CToken.TK_LBRA) {
            token = tokenizer.getNextToken(pcx);
            expression = new Expression(pcx);
            expression.parse(pcx);
            token = tokenizer.getCurrentToken(pcx);
            if (token.getType() != CToken.TK_RBRA) {
                pcx.fatalError("arrayの[]が閉じていません");
            }
            tokenizer.getNextToken(pcx);
        }
        array = Optional.ofNullable(expression);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

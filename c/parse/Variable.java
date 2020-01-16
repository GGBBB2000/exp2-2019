package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

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
        if (ident != null) {
            System.out.print("Variable (");
            ident.semanticCheck(pcx);
            array.ifPresentOrElse(
                    arr -> {
                        try {
                            if (ident.getCType().getType() != CType.T_int_arr) {
                                pcx.fatalError("Identが配列型ではありません");
                            }
                            System.out.print("Array[ ");
                            arr.semanticCheck(pcx);
                            if (arr.getCType().getType() != CType.T_int) {
                                pcx.fatalError("配列のインデックスにはintしか使えません");
                            }
                            this.setCType(CType.getCType(CType.T_int));

                            System.out.print("] ");
                        } catch (FatalErrorException e) {
                            e.printStackTrace();
                        }
                    },
                    () -> {
                        if (ident.getCType().getType() == CType.T_int_arr) {
                            try {
                                pcx.fatalError("arrayにインデックスが指定されていません");
                            } catch (FatalErrorException e) {
                                e.printStackTrace();
                            }
                        }
                        this.setCType(ident.getCType());
                    }
            );
            System.out.print(") ");
        }
        this.setConstant(false);
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

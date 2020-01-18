package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CType;

import java.io.PrintStream;
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
        ident = new Ident(pcx);
        ident.parse(pcx);
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);
        CParseRule tmp_array = null;
        if (token.getType() == CToken.TK_LBRA) {
            tmp_array = new Array(pcx);
            tmp_array.parse(pcx);
        }
        array = Optional.ofNullable(tmp_array);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null) {
            System.out.print("Variable (");
            ident.semanticCheck(pcx);
            final var isIntArr = ident.getCType().getType() == CType.T_int_arr;
            final var isPIntArr = ident.getCType().getType() == CType.T_pint_arr;
            array.ifPresentOrElse(
                    arr -> {
                        try {
                            if (!isIntArr && !isPIntArr) {
                                pcx.fatalError("Identが配列型ではありません");
                            }
                            arr.semanticCheck(pcx);
                            if (isIntArr) {
                                this.setCType(CType.getCType(CType.T_int));
                            } else {
                                this.setCType(CType.getCType(CType.T_pint));
                            }
                        } catch (FatalErrorException e) {
                            e.printStackTrace();
                        }
                    },
                    () -> {
                        if (isIntArr || isPIntArr) {
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
        this.setConstant(ident.isConstant());
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; variable starts");
        if (ident != null) {
            ident.codeGen(pcx);
        }
        //if (array != null) {array.codeGen(pcx); }
        array.ifPresent(arr -> {
            try {
                arr.codeGen(pcx);
            } catch (FatalErrorException e) {
                e.printStackTrace();
            }
        });
        o.println(";;; variable completes");
    }
}

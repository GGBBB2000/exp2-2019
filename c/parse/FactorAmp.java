package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

public class FactorAmp extends CParseRule {
    // factor ::= number
    private CToken op;
    private CParseRule numberPrimary;
    public FactorAmp(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;//&が最初か
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        // &の次の字句を読む
        CToken tk = ct.getNextToken(pcx);

        if (tk.getType() == CToken.TK_NUM) {
            numberPrimary = new Number(pcx);
        } else if (tk.getType() == CToken.TK_IDENT) {
            numberPrimary = new Primary(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "&の後ろはNumberかPrimaryです");
        }
        numberPrimary.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (numberPrimary != null) {
            if (numberPrimary instanceof Primary) {
                if (((Primary) numberPrimary).hasMultPrimary) {
                    pcx.fatalError("&の後ろに*は付けられません");
                }
            }
            numberPrimary.semanticCheck(pcx);
            final var type = numberPrimary.getCType().getType();
            int res = 0;
            switch (type) {
                case CType.T_int:
                    res = CType.T_pint;
                    break;
                case CType.T_int_arr:
                    res = CType.T_pint_arr;
                    break;
                default:
                    pcx.fatalError(type + "ポインタ型に&は付けられません");
            }
            this.setCType(CType.getCType(res));
            this.setConstant(numberPrimary.isConstant());    // number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        numberPrimary.codeGen(pcx);
    }
}

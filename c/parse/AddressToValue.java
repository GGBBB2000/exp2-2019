package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

public class AddressToValue extends CParseRule {
    CParseRule primary;

    public AddressToValue(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        primary = new Primary(pcx);
        primary.parse(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            this.setCType(primary.getCType());
            this.setConstant(false);
        }
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

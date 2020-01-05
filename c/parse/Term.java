package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class Term extends CParseRule {
    // term ::= factor
    private ArrayList<CParseRule> termMulDiv;
    public Term(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Factor.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        var factor = new Factor(pcx);
        factor.parse(pcx);
        termMulDiv = new ArrayList<CParseRule>();
        termMulDiv.add(factor);
        CParseRule mulDiv = null;
        System.out.println("hogehoge");
        while (TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
            if (tk.getType() == CToken.TK_MUL) {
                mulDiv = new TermMult(pcx);
            } else {
                mulDiv = new TermDiv(pcx);
            }
            mulDiv.parse(pcx);
            termMulDiv.add(mulDiv);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        //if (factor != null) {
        //    factor.semanticCheck(pcx);
        //    //this.setCType(factor.getCType());		// factor の型をそのままコピー
        //    //this.setConstant(factor.isConstant());
        //}

        //var left_type = factor.getCType();
        if (termMulDiv != null) {
            termMulDiv
                .stream()
                .forEach(term -> {
                    //term.semanticCheck(pcx);
                });
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; term starts");
        //if (factor != null) { factor.codeGen(pcx); }
        o.println(";;; term completes");
    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;
import java.util.ArrayList;

public class Term extends CParseRule {
    // term ::= factor
    private ArrayList<CParseRule> termMulDiv = new ArrayList<CParseRule>();
    public Term(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return Factor.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        var factor = new Factor(pcx);
        factor.parse(pcx);

        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        termMulDiv.add(factor);
        CParseRule mulDiv = null;
        while (TermMult.isFirst(tk) || TermDiv.isFirst(tk)) {
            if (tk.getType() == CToken.TK_MUL) {
                mulDiv = new TermMult(pcx);
            } else {
                mulDiv = new TermDiv(pcx);
            }
            tk = ct.getNextToken(pcx);
            mulDiv.parse(pcx);
            termMulDiv.add(mulDiv);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

        System.out.print("Term(");
        if (termMulDiv.size() >= 2) {
            var isMulDivConstant = false;
            for (var i = 0; i + 1 <= termMulDiv.size() - 1; i++) {
                var left = termMulDiv.get(i);
                var right = termMulDiv.get(i + 1);
                left.semanticCheck(pcx);
                right.semanticCheck(pcx);
                var leftType = left.getCType().getType();
                var rightType = right.getCType().getType();
                var result = leftType * rightType;
                if (result != CType.T_int) {
                    pcx.fatalError("乗除算にはポインタまたはarrayを用いることができません:");
                }
                isMulDivConstant = left.isConstant() & right.isConstant();
            }
            this.setCType(CType.getCType(CType.T_int)); // 乗除算のときはポインタは使えないため
            this.setConstant(isMulDivConstant);
        } else if (termMulDiv.size() == 1) {
            var fac = termMulDiv.get(0);
            fac.semanticCheck(pcx);
            this.setCType(fac.getCType());		// factor の型をそのままコピー
            this.setConstant(fac.isConstant());
        } else {
            pcx.fatalError("MULT/DIVの後ろにfactorがありません");
        }
        System.out.print(")");
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; term starts");
        //if (factor != null) { factor.codeGen(pcx); }
        if (termMulDiv != null) {
            termMulDiv.stream()
                    .forEach(term -> {
                        try {
                            term.codeGen(pcx);
                        } catch (FatalErrorException e) {
                            e.printStackTrace();
                        }
                    });
        }
        o.println(";;; term completes");
    }
}

package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.io.PrintStream;
import java.util.ArrayList;

public class Program extends CParseRule {
    // program ::= expression EOF
    private ArrayList<CParseRule> declaration = new ArrayList<>();
    private ArrayList<CParseRule> statement = new ArrayList<>();
    public Program(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {

        return Statement.isFirst(tk) || Declaration.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getCurrentToken(pcx);
        while (Declaration.isFirst(token)) {
            var tmp_decl = new Declaration(pcx);
            tmp_decl.parse(pcx);
            declaration.add(tmp_decl);
            token = tokenizer.getCurrentToken(pcx);
        }

        while (Statement.isFirst(token)) {
            var tmp_statement = new Statement(pcx);
            tmp_statement.parse(pcx);
            statement.add(tmp_statement);
            token = tokenizer.getCurrentToken(pcx);
        }
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        System.out.printf("tk.getType(): %d,CTOKEN.TK_EOF %d\n", tk.getType(), CToken.TK_EOF);
        if (tk.getType() != CToken.TK_EOF) {
            pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        statement.forEach(s -> {
            try {
                s.semanticCheck(pcx);
            } catch (FatalErrorException e) {
                e.printStackTrace();
            }
        });
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; program starts");
        o.println("\t. = 0x100");
        o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
        // ここには将来、宣言に対するコード生成が必要
        if (statement != null) {
            o.println("__START:");
            o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
            //statement.codeGen(pcx);
            statement.forEach(s -> {
                try {
                    s.codeGen(pcx);
                } catch (FatalErrorException e) {
                    e.printStackTrace();
                }
            });
            o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
        }
        o.println("\tHLT\t\t\t; ProgramNode:");
        o.println("\t.END\t\t\t; ProgramNode:");
        o.println(";;; program completes");
    }
}

package lang.c;

import lang.FatalErrorException;
import lang.IOContext;
import lang.c.parse.ConditionExpression;

public class ConditionTest {
    public static void main(String[] args) {
        String inFile = args[0];
        var ioCtx = new IOContext(inFile, System.out, System.err);
        var tknz = new CTokenizer(new CTokenRule());
        var pcx = new CParseContext(ioCtx, tknz);
        try {
            CTokenizer ct = pcx.getTokenizer();
            CToken tk = ct.getNextToken(pcx);
            if (ConditionExpression.isFirst(tk)) {
                CParseRule parseTree = new ConditionExpression(pcx);
                parseTree.parse(pcx); // 構文解析
                if (pcx.hasNoError()) parseTree.semanticCheck(pcx); // 意味解析
                //if (pcx.hasNoError()) parseTree.codeGen(pcx); // コード生成
                pcx.errorReport();
            } else {
                System.out.println(tk.getType());
                pcx.fatalError(tk.toExplainString() + "プログラムの先頭にゴミがあります");
            }
        } catch (FatalErrorException e) {
            e.printStackTrace();
        }
    }
}

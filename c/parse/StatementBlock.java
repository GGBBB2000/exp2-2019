package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;

import java.util.ArrayList;

public class StatementBlock extends CParseRule {
    ArrayList<CParseRule> statementList = new ArrayList<>();

    public static boolean isFirst(CToken token) {
        return token.getType() == CToken.TK_LCUR;
    }

    public StatementBlock(CParseContext pcx) {}

    @Override
    public void parse(CParseContext pcx) throws FatalErrorException {
        final var tokenizer = pcx.getTokenizer();
        var token = tokenizer.getNextToken(pcx);
        while (Statement.isFirst(token)) {
            var statement = new Statement(pcx);
            statement.parse(pcx);
            statementList.add(statement);
            token = tokenizer.getCurrentToken(pcx);
        }
        if (token.getType() != CToken.TK_RCUR) {
            pcx.fatalError(token.toExplainString() + "{}が閉じていません");
        }
        tokenizer.getNextToken(pcx);
    }

    @Override
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        statementList.forEach(statement -> {
            try {
                statement.semanticCheck(pcx);
            } catch (FatalErrorException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void codeGen(CParseContext pcx) throws FatalErrorException {

    }
}

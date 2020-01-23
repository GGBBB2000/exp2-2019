package lang.c;

import java.util.HashMap;

public class CTokenRule extends HashMap<String, Object> {
	private static final long serialVersionUID = 1139476411716798082L;

	public CTokenRule() {
        put("int", CToken.TK_INT);
        put("const", CToken.TK_CONST);
        put("true", CToken.TK_TRUE);
        put("false", CToken.TK_FALSE);
        put("input", CToken.TK_INPUT);
        put("output", CToken.TK_OUTPUT);
        put("if", CToken.TK_IF);
        put("else", CToken.TK_ELSE);
        put("do", CToken.TK_DO);
        put("while", CToken.TK_WHILE);
    }
}

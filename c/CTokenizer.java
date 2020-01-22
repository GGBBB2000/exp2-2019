package lang.c;

import lang.Tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
    @SuppressWarnings("unused")
    private CTokenRule rule;
    private int lineNo, colNo;
    private char backCh;
    private boolean backChExist = false;

    public CTokenizer(CTokenRule rule) {
        this.rule = rule;
        lineNo = 1;
        colNo = 1;
    }

    private InputStream in;
    private PrintStream err;

    private char readChar() {
        char ch;
        if (backChExist) {
            ch = backCh;
            backChExist = false;
        } else {
            try {
                ch = (char) in.read();
            } catch (IOException e) {
                e.printStackTrace(err);
                ch = (char) -1;
            }
        }
        ++colNo;
        if (ch == '\n') {
            colNo = 1;
            ++lineNo;
        }
        //System.out.print("'"+ch+"'("+(int)ch+")");
        return ch;
    }

    private void backChar(char c) {
        backCh = c;
        backChExist = true;
        --colNo;
        if (c == '\n') {
            --lineNo;
        }
    }

    // 現在読み込まれているトークンを返す
    private CToken currentTk = null;

    public CToken getCurrentToken(CParseContext pctx) {
        return currentTk;
    }

    // 次のトークンを読んで返す
    public CToken getNextToken(CParseContext pctx) {
        in = pctx.getIOContext().getInStream();
        err = pctx.getIOContext().getErrStream();
        currentTk = readToken();
        //		System.out.println("Token='" + currentTk.toString());
        return currentTk;
    }

    private CToken readToken() {
        CToken tk = null;
        char ch;
        int startCol = colNo;
        boolean isHex = false;
        StringBuffer text = new StringBuffer();

        int state = 0;
        boolean accept = false;
        while (!accept) {
            switch (state) {
                case 0:                    // 初期状態
                    ch = readChar();
                    if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                        state = 0;
                    } else if (ch == (char) -1) {    // EOF
                        startCol = colNo - 1;
                        state = 1;
                    } else if (ch == '0') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 11;
                    } else if (ch > '0' && ch <= '9') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 3;
                    } else if (ch == '+') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 4;
                    } else if (ch == '-') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 5;
                    } else if (ch == '/') {
                        text.append(ch);
                        startCol = colNo - 1;
                        state = 6;
                    } else if (ch == '&') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 10;
                    } else if (ch == '*') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 12;
                    } else if (ch == '(') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 13;
                    } else if (ch == ')') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 14;
                    } else if (ch == '[') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 16;
                    } else if (ch == ']') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 17;
                    } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 18;
                    } else if (ch == '=') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 19;
                    } else if (ch == ';') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 20;
                    } else if (ch == ',') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 21;
                    } else if (ch == '<') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 22;
                    } else if (ch == '>') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 23;
                    } else if (ch == '!') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 24;
                    } else {            // ヘンな文字を読んだ
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 2;
                    }
                    break;
                case 1:                    // EOFを読んだ
                    tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
                    accept = true;
                    break;
                case 2:                    // ヘンな文字を読んだ
                    tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
                    accept = true;
                    break;
                case 3:                    // 数（10進数）の開始
                    ch = readChar();
                    if (Character.isDigit(ch) ||
                            (isHex && ((ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')))) {
                        text.append(ch);
                    } else {
                        // 数の終わり
                        try {
                            if (Math.abs(Integer.decode(text.toString())) > 0xFFFF) {
                                tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
                            } else {
                                backChar(ch);
                                tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
                            }
                            accept = true;
                        } catch (NumberFormatException e) {
                            state = 2;
                        }
                    }
                    break;
                case 4:                    // +を読んだ
                    tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
                    accept = true;
                    break;
                case 5: // - を読んだ
                    tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
                    accept = true;
                    break;
                case 6: // '/'を読んだ(まだ割り算は考えない)
                    ch = readChar();
                    if (ch == '/') {
                        text.append(ch);
                        state = 7;
                    } else if (ch == '*') {
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 8;
                    } else if (ch == ' ') {
                        state = 15;
                    } else if (ch == (char) -1) {
                        backChar(ch);
                        state = 0;
                    } /*else if (Character.isDigit(ch)
                            || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        backChar(ch);
                        tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
                    } */ else {
                        //text.append(ch);
                        accept = true;
                        backChar(ch);
                        tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
                    }
                    break;

                case 7: // <- このタイプのコメントの解析
                    ch = readChar();
                    if (ch == '\n') {
                        state = 0;
                        text = new StringBuffer();
                    } else if (ch == (char) -1) {
                        backChar(ch);
                        state = 1;
                    }
                    break;

                case 8: /* コメント本文の解析(*で違う状態へ) */
                    ch = readChar();
                    if (ch == '*') {
                        state = 9;
                    } else if (ch == (char) -1) {
                        state = 2;
                    }
                    text.append(ch);
                    break;
                case 9: /* コメントのこれの解析 -> */
                    ch = readChar();
                    if (ch == '*') {
                        text.append(ch);
                    } else if (ch == '/') {
                        state = 0;
                        text = new StringBuffer();
                    } else {
                        text.append(ch);
                        state = 8;
                    }
                    break;
                case 10: /* &を読んだ (現状では)ANDではない*/
                    tk = new CToken(CToken.TK_AMP, lineNo, startCol, text.toString());
                    accept = true;
                    break;
                case 11: // 16/8進数を読む
                    ch = readChar();
                    if (ch == 'x' || (ch >= '0' && ch <= '9')) {
                        startCol = colNo - 1;
                        isHex = true;
                        text.append(ch);
                        state = 3;
                    } else {

                        backChar(ch);
                        tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
                        accept = true;
                    }
                    break;
                case 12: // * を読んだ
                    tk = new CToken(CToken.TK_MUL, lineNo, startCol, "*");
                    accept = true;
                    break;
                case 13: // ( を読んだ
                    tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
                    accept = true;
                    break;
                case 14: // ) を読んだ
                    tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
                    accept = true;
                    break;
                case 15: // 割り算
                    ch = readChar();
                    if (ch == ' ') {
                    } else if (Character.isDigit(ch)
                            || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        backChar(ch);
                        tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
                        accept = true;
                    } else {
                        text.append(ch);
                        state = 2;
                    }
                    break;
                case 16: // ( を読んだ
                    tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
                    accept = true;
                    break;
                case 17: // ) を読んだ
                    tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
                    accept = true;
                    break;
                case 18: // IDENTを読む
                    ch = readChar();
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                            || (ch >= '0' && ch <= '9')) {
                        text.append(ch);
                    } else {
                        backChar(ch);
                        final var txt = text.toString();
                        var tokenType = Optional.ofNullable((Integer) rule.get(txt))
                                .orElse(CToken.TK_IDENT);
                        tk = new CToken(tokenType, lineNo, startCol, txt);
                        accept = true;
                        break;
                    }
                    break;
                case 19: // = を読んだ
                    ch = readChar();
                    tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
                    if (ch == '=') {
                        tk = new CToken(CToken.TK_EQ, lineNo, startCol, "==");
                    } else {
                        backChar(ch);
                    }
                    accept = true;
                    break;
                case 20:
                    tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
                    accept = true;
                    break;
                case 21:
                    tk = new CToken(CToken.TK_COMMA, lineNo, startCol, ",");
                    accept = true;
                    break;
                case 22:
                    ch = readChar();
                    tk = new CToken(CToken.TK_LT, lineNo, startCol, "<");
                    if (ch == '=') {
                        tk = new CToken(CToken.TK_LE, lineNo, startCol, "<=");
                    } else {
                        backChar(ch);
                    }
                    accept = true;
                    break;
                case 23:
                    ch = readChar();
                    tk = new CToken(CToken.TK_GT, lineNo, startCol, ">");
                    if (ch == '=') {
                        tk = new CToken(CToken.TK_GE, lineNo, startCol, ">=");
                    } else {
                        backChar(ch);
                    }
                    accept = true;
                    break;
                case 24:
                    ch = readChar();
                    if (ch == '=') {
                        tk = new CToken(CToken.TK_NE, lineNo, startCol, "!=");
                        accept = true;
                    } else {
                        text.append(ch);
                        state = 2;
                    }
                    break;
            }
            //System.out.println(state);
        }
        return tk;
    }
}

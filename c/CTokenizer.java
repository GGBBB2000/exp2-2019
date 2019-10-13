package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
    @SuppressWarnings("unused")
    private CTokenRule	rule;
    private int			lineNo, colNo;
    private char		backCh;
    private boolean		backChExist = false;

    public CTokenizer(CTokenRule rule) {
        this.rule = rule;
        lineNo = 1; colNo = 1;
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
        if (ch == '\n')  { colNo = 1; ++lineNo; }
        //System.out.print("'"+ch+"'("+(int)ch+")");
        return ch;
    }
    private void backChar(char c) {
        backCh = c;
        backChExist = true;
        --colNo;
        if (c == '\n') { --lineNo; }
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
        int  startCol = colNo;
        StringBuffer text = new StringBuffer();

        int state = 0;
        boolean accept = false;
        while (!accept) {
            switch (state) {
                case 0:					// 初期状態
                    ch = readChar();
                    if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                    } else if (ch == (char) -1) {	// EOF
                        startCol = colNo - 1;
                        state = 1;
                    } else if (ch >= '0' && ch <= '9') {
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
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 6;
                    } else {			// ヘンな文字を読んだ
                        startCol = colNo - 1;
                        text.append(ch);
                        state = 2;
                    }
                    break;
                case 1:					// EOFを読んだ
                    tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
                    accept = true;
                    break;
                case 2:					// ヘンな文字を読んだ
                    tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
                    accept = true;
                    break;
                case 3:					// 数（10進数）の開始
                    ch = readChar();
                    if (Character.isDigit(ch)) {
                        text.append(ch);
                    } else {
                        // 数の終わり
                        backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
                        tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
                        accept = true;
                    }
                    break;
                case 4:					// +を読んだ
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
                        text.append(ch);
                        state = 8;
                    //} else { //このままだと割り算できない
                    }
                    break;

                case 7: // <- このタイプのコメントの解析
                    ch = readChar();
                    if (ch == '\n') {
                        tk = new CToken(CToken.TK_COMMENT, lineNo, startCol, text.toString());
                        accept = true;
                    } else if (ch == - 1) {
                        backChar(ch);
                        tk = new CToken(CToken.TK_COMMENT, lineNo, startCol, text.toString());
                        accept = true;
                    } else {
                        text.append(ch);
                    }
                    break;

                case 8: /* コメント本文の解析(*で違う状態へ) */
                    ch = readChar();
                    if (ch == '*') {
                        text.append(ch);
                        state = 9;
                    } else if((int)ch == 10) {
                        //text.append(ch);
                        backChar(ch);
                        state = 2;
                        //accept = true;
                        //tk = new CToken(CToken.TK_COMMENT, lineNo, startCol, text.toString());
                    } else {
                        text.append(ch);
                    }
                    break;
                case 9: /* コメントのこれの解析 -> */
                    ch = readChar();
                    if (ch == '\0') { 
                        /*TODO 途中でファイルが終わったときの
                         * 状態を作るべきか？ */
                        //text.append(ch);
                        backChar(ch);
                        tk = new CToken(CToken.TK_COMMENT, lineNo, startCol, text.toString());
                        state = 2;
                    } else if(ch == '*') {
                        text.append(ch);
                    } else if(ch == '/') {
                        text.append(ch);
                        accept = true;
                        tk = new CToken(CToken.TK_COMMENT, lineNo, startCol, text.toString());
                    } else {
                        backChar(ch);
                        state = 2;
                    }
                    break;
            }
        }
        System.out.println("hugahuga" + tk.getText());
        return tk;
    }
}

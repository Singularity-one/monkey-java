package com.monkey.lexer;

import com.monkey.token.Token;
import com.monkey.token.TokenType;

/**
 * Lexer（詞法分析器）將輸入字串轉換為 token 序列
 */
public class Lexer {
    private final String input;
    private int position;      // 當前位置（指向當前字元）
    private int readPosition;  // 讀取位置（指向下一個字元）
    private char ch;           // 當前檢查的字元

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 0;
        // 讀取第一個字元
        readChar();
    }

    /**
     * 讀取下一個字元並前進位置
     */
    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0; // ASCII 碼的 NUL，表示還沒讀取或已到結尾
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        readPosition++;
    }

    /**
     * 偷看下一個字元但不前進位置
     */
    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        }
        return input.charAt(readPosition);
    }

    /**
     * 獲取下一個 token
     */
    public Token nextToken() {
        Token tok;

        // 跳過空白字元
        skipWhitespace();

        switch (ch) {
            case '=' -> {
                if (peekChar() == '=') {
                    char currentCh = ch;
                    readChar();
                    tok = new Token(TokenType.EQ, "" + currentCh + ch);
                } else {
                    tok = new Token(TokenType.ASSIGN, ch);
                }
            }
            case '+' -> tok = new Token(TokenType.PLUS, ch);
            case '-' -> tok = new Token(TokenType.MINUS, ch);
            case '!' -> {
                if (peekChar() == '=') {
                    char currentCh = ch;
                    readChar();
                    tok = new Token(TokenType.NOT_EQ, "" + currentCh + ch);
                } else {
                    tok = new Token(TokenType.BANG, ch);
                }
            }
            case '/' -> tok = new Token(TokenType.SLASH, ch);
            case '*' -> tok = new Token(TokenType.ASTERISK, ch);
            case '<' -> tok = new Token(TokenType.LT, ch);
            case '>' -> tok = new Token(TokenType.GT, ch);
            case ';' -> tok = new Token(TokenType.SEMICOLON, ch);
            case ',' -> tok = new Token(TokenType.COMMA, ch);
            case '(' -> tok = new Token(TokenType.LPAREN, ch);
            case ')' -> tok = new Token(TokenType.RPAREN, ch);
            case '{' -> tok = new Token(TokenType.LBRACE, ch);
            case '}' -> tok = new Token(TokenType.RBRACE, ch);
            case '[' -> tok = new Token(TokenType.LBRACKET, ch);
            case ']' -> tok = new Token(TokenType.RBRACKET, ch);
            case ':' -> tok = new Token(TokenType.COLON, ch);
            case '"' -> tok = new Token(TokenType.STRING, readString());
            case 0 -> tok = new Token(TokenType.EOF, "");
            default -> {
                if (isLetter(ch)) {
                    String literal = readIdentifier();
                    TokenType type = Token.lookupIdent(literal);
                    return new Token(type, literal);
                } else if (isDigit(ch)) {
                    String literal = readNumber();
                    return new Token(TokenType.INT, literal);
                } else {
                    tok = new Token(TokenType.ILLEGAL, ch);
                }
            }
        }

        readChar();
        return tok;
    }

    /**
     * 讀取識別符號（變數名、函數名等）
     */
    private String readIdentifier() {
        int startPosition = position;
        while (isLetter(ch)) {
            readChar();
        }
        return input.substring(startPosition, position);
    }

    /**
     * 讀取數字
     */
    private String readNumber() {
        int startPosition = position;
        while (isDigit(ch)) {
            readChar();
        }
        return input.substring(startPosition, position);
    }

    /**
     * 跳過空白字元（空格、tab、換行等）
     */
    private void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
    }

    /**
     * 判斷是否為字母（包括底線）
     */
    private boolean isLetter(char ch) {
        return ('a' <= ch && ch <= 'z') ||
                ('A' <= ch && ch <= 'Z') ||
                ch == '_';
    }

    /**
     * 判斷是否為數字
     */
    private boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    /**
     * 讀取字串（處理引號內的內容）
     */
    private String readString() {
        int startPosition = position + 1; // 跳過開頭的 "
        while (true) {
            readChar();
            if (ch == '"' || ch == 0) {
                break;
            }
        }
        return input.substring(startPosition, position);
    }
}

package com.monkey.token;

/**
 * TokenType 定義 Monkey 語言中所有的 token 類型
 */
public enum TokenType {
    // 特殊 token
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // 識別符號與字面值
    IDENT("IDENT"),      // 變數名、函數名：add, foobar, x, y, ...
    INT("INT"),          // 整數：1343456

    // 運算符
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    BANG("!"),
    ASTERISK("*"),
    SLASH("/"),

    LT("<"),
    GT(">"),

    EQ("=="),
    NOT_EQ("!="),

    // 分隔符
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // 關鍵字
    FUNCTION("FUNCTION"),
    LET("LET"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN");

    private final String literal;

    TokenType(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return literal;
    }
}
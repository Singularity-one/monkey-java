package com.monkey.token;

import java.util.Map;
import java.util.Objects;

/**
 * Token 代表詞法分析器識別出的一個單詞
 */
public class Token {
    private final TokenType type;
    private final String literal;

    // 關鍵字映射表
    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "fn", TokenType.FUNCTION,
            "let", TokenType.LET,
            "true", TokenType.TRUE,
            "false", TokenType.FALSE,
            "if", TokenType.IF,
            "else", TokenType.ELSE,
            "return", TokenType.RETURN
    );

    public Token(TokenType type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    /**
     * 方便的構造方法，用於單字元 token
     */
    public Token(TokenType type, char ch) {
        this.type = type;
        this.literal = String.valueOf(ch);
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    /**
     * 查找識別符號是否為關鍵字
     * 如果是關鍵字，返回對應的 TokenType
     * 否則返回 IDENT
     */
    public static TokenType lookupIdent(String ident) {
        return KEYWORDS.getOrDefault(ident, TokenType.IDENT);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", literal='" + literal + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, literal);
    }
}
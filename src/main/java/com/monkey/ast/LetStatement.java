package com.monkey.ast;

import com.monkey.token.Token;

/**
 * LetStatement 表示 let 語句
 * let <identifier> = <expression>;
 */
public class LetStatement implements Statement {
    private final Token token;       // 'let' token
    private Identifier name;         // 變量名
    private Expression value;        // 值表達式

    public LetStatement(Token token) {
        this.token = token;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public void statementNode() {
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral());
        sb.append(" ");
        sb.append(name.string());
        sb.append(" = ");
        if (value != null) {
            sb.append(value.string());
        }
        sb.append(";");
        return sb.toString();
    }
}
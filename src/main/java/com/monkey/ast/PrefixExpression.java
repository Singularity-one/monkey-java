package com.monkey.ast;

import com.monkey.token.Token;

/**
 * PrefixExpression 表示前綴表達式
 * 例如: -5, !true
 */
public class PrefixExpression implements Expression {
    private final Token token;      // 前綴運算符 token (如 !, -)
    private final String operator;  // 運算符字符串
    private Expression right;       // 右側表達式

    public PrefixExpression(Token token, String operator) {
        this.token = token;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public void expressionNode() {
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
        return "(" + operator + right.string() + ")";
    }
}
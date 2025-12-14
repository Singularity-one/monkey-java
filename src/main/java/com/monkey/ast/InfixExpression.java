package com.monkey.ast;
import com.monkey.token.Token;

/**
 * InfixExpression 代表中綴表達式（二元運算）
 * 例如：5 + 5, x * y
 */
public class InfixExpression implements Expression {
    private final Token token; // 運算符 token，例如 +
    private Expression left;
    private final String operator;
    private Expression right;

    public InfixExpression(Token token, Expression left, String operator) {
        this.token = token;
        this.left = left;
        this.operator = operator;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        return "(" + left.string() + " " + operator + " " + right.string() + ")";
    }
}
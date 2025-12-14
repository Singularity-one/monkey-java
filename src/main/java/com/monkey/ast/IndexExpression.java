package com.monkey.ast;
import com.monkey.token.Token;

/**
 * IndexExpression 代表索引表達式
 * 例如：myArray[0], myHash["key"]
 */
public class IndexExpression implements Expression {
    private final Token token; // [ token
    private final Expression left;
    private Expression index;

    public IndexExpression(Token token, Expression left) {
        this.token = token;
        this.left = left;
    }

    public void setIndex(Expression index) {
        this.index = index;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getIndex() {
        return index;
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
        return "(" + left.string() + "[" + index.string() + "])";
    }
}

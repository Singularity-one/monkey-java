package com.monkey.ast;

import com.monkey.token.Token;

/**
 * IfExpression 代表 if 表達式
 * 例如：if (x < y) { x } else { y }
 */
public class IfExpression implements Expression {
    private final Token token; // if token
    private Expression condition;
    private BlockStatement consequence;
    private BlockStatement alternative;

    public IfExpression(Token token) {
        this.token = token;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }

    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
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
        StringBuilder out = new StringBuilder();
        out.append("if");
        out.append(condition.string());
        out.append(" ");
        out.append(consequence.string());

        if (alternative != null) {
            out.append("else ");
            out.append(alternative.string());
        }

        return out.toString();
    }
}

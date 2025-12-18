package com.monkey.ast;

import com.monkey.token.Token;

/**
 * IfExpression 表示 if 表達式
 * if (<condition>) <consequence> else <alternative>
 */
public class IfExpression implements Expression {
    private final Token token;                // 'if' token
    private Expression condition;             // 條件表達式
    private BlockStatement consequence;       // if 分支
    private BlockStatement alternative;       // else 分支 (可選)

    public IfExpression(Token token) {
        this.token = token;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
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
        StringBuilder sb = new StringBuilder();
        sb.append("if");
        sb.append(condition.string());
        sb.append(" ");
        sb.append(consequence.string());

        if (alternative != null) {
            sb.append("else ");
            sb.append(alternative.string());
        }

        return sb.toString();
    }
}
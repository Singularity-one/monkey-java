package com.monkey.ast;

import com.monkey.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CallExpression 代表函數調用表達式
 * 例如：add(2, 3)
 */
public class CallExpression implements Expression {
    private final Token token; // ( token
    private final Expression function; // 函數識別符號或函數字面值
    private final List<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
        this.arguments = new ArrayList<>();
    }

    public void addArgument(Expression arg) {
        arguments.add(arg);
    }

    public Expression getFunction() {
        return function;
    }

    public List<Expression> getArguments() {
        return arguments;
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

        String args = arguments.stream()
                .map(Expression::string)
                .collect(Collectors.joining(", "));

        out.append(function.string());
        out.append("(");
        out.append(args);
        out.append(")");

        return out.toString();
    }
}

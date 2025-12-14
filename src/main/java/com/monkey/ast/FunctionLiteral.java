package com.monkey.ast;

import com.monkey.token.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FunctionLiteral 代表函數字面值
 * 例如：fn(x, y) { x + y; }
 */
public class FunctionLiteral implements Expression {
    private final Token token; // fn token
    private final List<Identifier> parameters;
    private BlockStatement body;

    public FunctionLiteral(Token token) {
        this.token = token;
        this.parameters = new ArrayList<>();
    }

    public void addParameter(Identifier param) {
        parameters.add(param);
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
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

        String params = parameters.stream()
                .map(Identifier::string)
                .collect(Collectors.joining(", "));

        out.append(tokenLiteral());
        out.append("(");
        out.append(params);
        out.append(") ");
        out.append(body.string());

        return out.toString();
    }
}

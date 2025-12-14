package com.monkey.object;

import com.monkey.ast.BlockStatement;
import com.monkey.ast.Identifier;
import com.monkey.evaluator.Environment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FunctionObject 代表函數
 * 包含參數、函數體和環境（用於閉包）
 */
public class FunctionObject implements MonkeyObject {
    private final List<Identifier> parameters;
    private final BlockStatement body;
    private final Environment env;

    public FunctionObject(List<Identifier> parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public Environment getEnv() {
        return env;
    }

    @Override
    public ObjectType type() {
        return ObjectType.FUNCTION;
    }

    @Override
    public String inspect() {
        String params = parameters.stream()
                .map(Identifier::string)
                .collect(Collectors.joining(", "));

        return "fn(" + params + ") {\n" + body.string() + "\n}";
    }
}

package com.monkey.ast;
import com.monkey.token.Token;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HashLiteral 代表雜湊字面值
 * 例如：{"name": "John", "age": 30}
 */
public class HashLiteral implements Expression {
    private final Token token; // { token
    private final Map<Expression, Expression> pairs;

    public HashLiteral(Token token) {
        this.token = token;
        this.pairs = new LinkedHashMap<>();
    }

    public void addPair(Expression key, Expression value) {
        pairs.put(key, value);
    }

    public Map<Expression, Expression> getPairs() {
        return pairs;
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
        String pairsStr = pairs.entrySet().stream()
                .map(entry -> entry.getKey().string() + ":" + entry.getValue().string())
                .collect(Collectors.joining(", "));
        return "{" + pairsStr + "}";
    }
}

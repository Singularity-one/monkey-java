package com.monkey.ast;
import com.monkey.token.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ArrayLiteral 代表陣列字面值
 * 例如：[1, 2, 3, 4]
 */
public class ArrayLiteral implements Expression {
    private final Token token; // [ token
    private final List<Expression> elements;

    public ArrayLiteral(Token token) {
        this.token = token;
        this.elements = new ArrayList<>();
    }

    public void addElement(Expression element) {
        elements.add(element);
    }

    public List<Expression> getElements() {
        return elements;
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
        String elementsStr = elements.stream()
                .map(Expression::string)
                .collect(Collectors.joining(", "));
        return "[" + elementsStr + "]";
    }
}

package com.monkey.object;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ArrayObject 代表陣列
 */
public class ArrayObject implements MonkeyObject {
    private final List<MonkeyObject> elements;

    public ArrayObject(List<MonkeyObject> elements) {
        this.elements = elements;
    }

    public List<MonkeyObject> getElements() {
        return elements;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ARRAY;
    }

    @Override
    public String inspect() {
        String elementsStr = elements.stream()
                .map(MonkeyObject::inspect)
                .collect(Collectors.joining(", "));
        return "[" + elementsStr + "]";
    }

    @Override
    public HashKey hashKey() {
        return null;
    }
}
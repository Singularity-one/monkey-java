package com.monkey.object;

import java.util.Objects;

/**
 * IntegerObject 代表整數值
 */
public class IntegerObject implements MonkeyObject {
    private final long value;

    public IntegerObject(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerObject that = (IntegerObject) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

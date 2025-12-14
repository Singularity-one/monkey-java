package com.monkey.object;
import java.util.Objects;

/**
 * HashKey 代表雜湊表的鍵
 */
public class HashKey {
    private final ObjectType type;
    private final int value;

    public HashKey(ObjectType type, int value) {
        this.type = type;
        this.value = value;
    }

    public ObjectType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashKey hashKey = (HashKey) o;
        return value == hashKey.value && type == hashKey.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
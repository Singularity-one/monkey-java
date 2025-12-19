package com.monkey.object;

/**
 * NullObject 表示 null 值
 * Chapter 4: Conditionals
 */
public class NullObject implements MonkeyObject {
    // 單例模式
    public static final NullObject NULL = new NullObject();

    public NullObject() {
    }

    @Override
    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }

    @Override
    public HashKey hashKey() {
        // null 總是有相同的 hash key
        return new HashKey(type(), 0L);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullObject;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return inspect();
    }
}
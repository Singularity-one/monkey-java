package com.monkey.object;

/**
 * BooleanObject 表示布爾值對象
 * Chapter 3: Compiling Expressions
 */
public class BooleanObject implements MonkeyObject, Hashable{
    private final boolean value;

    // 單例模式 - 只有兩個實例
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);

    private BooleanObject(boolean value) {
        this.value = value;
    }

    /**
     * 從 Java boolean 獲取 BooleanObject
     */
    public static BooleanObject valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public HashKey hashKey() {
        long hash = value ? 1L : 0L;
        return new HashKey(type(), hash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BooleanObject)) return false;
        BooleanObject other = (BooleanObject) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return inspect();
    }
}

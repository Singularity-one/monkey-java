package com.monkey.object;

/**
 * BooleanObject 代表布林值
 * 使用單例模式節省記憶體
 */
public class BooleanObject implements MonkeyObject , Hashable{
    private final boolean value;

    // 單例實例
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);

    private BooleanObject(boolean value) {
        this.value = value;
    }

    /**
     * 獲取對應的 BooleanObject 實例
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
        return new HashKey(type(), value ? 1 : 0);
    }
}

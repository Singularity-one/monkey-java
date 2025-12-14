package com.monkey.object;
/**
 * ReturnValue 包裝 return 語句返回的值
 * 用於在函數中停止執行並返回值
 */
public class ReturnValue implements MonkeyObject {
    private final MonkeyObject value;

    public ReturnValue(MonkeyObject value) {
        this.value = value;
    }

    public MonkeyObject getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.RETURN_VALUE;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }

    @Override
    public HashKey hashKey() {
        return null;
    }
}
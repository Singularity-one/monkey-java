package com.monkey.object;

/**
 * NullObject 代表 null 值
 * 使用單例模式
 */
public class NullObject implements MonkeyObject {

    // 單例實例
    public static final NullObject NULL = new NullObject();

    private NullObject() {
    }

    @Override
    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }
}

package com.monkey.object;

/**
 * ObjectType 定義所有物件類型
 */
public enum ObjectType {
    INTEGER("INTEGER"),
    BOOLEAN("BOOLEAN"),
    NULL("NULL"),
    RETURN_VALUE("RETURN_VALUE"),
    ERROR("ERROR"),
    FUNCTION("FUNCTION"),
    STRING("STRING"),
    BUILTIN("BUILTIN"),
    ARRAY("ARRAY"),
    HASH("HASH"),
    COMPILED_FUNCTION("COMPILED_FUNCTION");

    private final String value;

    ObjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

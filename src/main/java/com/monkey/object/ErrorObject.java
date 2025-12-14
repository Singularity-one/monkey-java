package com.monkey.object;

/**
 * ErrorObject 代表運行時錯誤
 */
public class ErrorObject implements MonkeyObject {
    private final String message;

    public ErrorObject(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ERROR;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }
}

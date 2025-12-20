package com.monkey.object;

/**
 * ErrorObject 代表運行時錯誤
 */
public class ErrorObject implements MonkeyObject {
    private final String message;

    public ErrorObject(String message) {
        this.message = message;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ERROR;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }

    @Override
    public HashKey hashKey() {
        return null;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ErrorObject)) return false;
        ErrorObject other = (ErrorObject) obj;
        return message.equals(other.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}

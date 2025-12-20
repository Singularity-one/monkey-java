package com.monkey.object;

/**
 * BuiltinObject 表示內建函數
 * Chapter 8: Built-in Functions
 */
public class BuiltinObject implements MonkeyObject {

    @FunctionalInterface
    public interface BuiltinFunction {
        MonkeyObject apply(MonkeyObject... args);
    }

    private final BuiltinFunction fn;

    public BuiltinObject(BuiltinFunction fn) {
        this.fn = fn;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BUILTIN;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }

    @Override
    public HashKey hashKey() {
        return null; // 內建函數不可雜湊
    }

    public BuiltinFunction getFn() {
        return fn;
    }
}
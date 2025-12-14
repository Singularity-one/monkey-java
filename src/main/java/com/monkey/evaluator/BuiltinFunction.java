package com.monkey.evaluator;
import com.monkey.object.HashKey;
import com.monkey.object.MonkeyObject;
import com.monkey.object.ObjectType;

import java.util.List;

/**
 * BuiltinFunction 代表內建函數
 */
public class BuiltinFunction implements MonkeyObject {
    private final BuiltinFn fn;

    @FunctionalInterface
    public interface BuiltinFn {
        MonkeyObject apply(List<MonkeyObject> args);
    }

    public BuiltinFunction(BuiltinFn fn) {
        this.fn = fn;
    }

    public BuiltinFn getFn() {
        return fn;
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
        return null;
    }
}

package com.monkey.object;

import java.util.Arrays;

/**
 * ClosureObject 表示閉包
 * Chapter 9: Closures
 *
 * 閉包是一個函數和它引用的自由變量的組合
 */
public class ClosureObject implements MonkeyObject {
    private final CompiledFunctionObject fn;
    private final MonkeyObject[] free;  // 自由變量

    public ClosureObject(CompiledFunctionObject fn) {
        this(fn, new MonkeyObject[0]);
    }

    public ClosureObject(CompiledFunctionObject fn, MonkeyObject[] free) {
        this.fn = fn;
        this.free = free;
    }

    @Override
    public ObjectType type() {
        return ObjectType.CLOSURE;
    }

    @Override
    public String inspect() {
        return String.format("Closure[%s]", Integer.toHexString(hashCode()));
    }

    @Override
    public HashKey hashKey() {
        return null;  // 閉包不可雜湊
    }

    public CompiledFunctionObject getFn() {
        return fn;
    }

    public MonkeyObject[] getFree() {
        return free;
    }

    @Override
    public String toString() {
        return String.format("Closure{fn=%s, free=%s}", fn, Arrays.toString(free));
    }
}
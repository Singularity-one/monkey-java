package com.monkey.compiler;

/**
 * SymbolScope 符號作用域
 * Chapter 7: Functions (擴展)
 */
public enum SymbolScope {
    GLOBAL("GLOBAL"),
    LOCAL("LOCAL"),      // ⭐ 第七章新增
    BUILTIN("BUILTIN"),  // ⭐ 第八章新增
    FREE("FREE"), // ⭐ 第九章新增
    FUNCTION("FUNCTION");

    private final String value;

    SymbolScope(String value) {
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
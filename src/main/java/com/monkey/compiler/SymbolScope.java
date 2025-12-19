package com.monkey.compiler;

/**
 * SymbolScope 表示符號的作用域
 * Chapter 5: Keeping Track of Names
 */
public enum SymbolScope {
    GLOBAL,    // 全局作用域
    LOCAL,     // 局部作用域 (Chapter 7)
    BUILTIN,   // 內建函數作用域 (Chapter 8)
    FREE       // 自由變量作用域 (Chapter 9)
}
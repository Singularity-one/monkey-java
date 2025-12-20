package com.monkey.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable 符號表
 * Chapter 7: Functions (擴展)
 *
 * 新增功能:
 * - 局部作用域支持
 * - 嵌套作用域
 */
public class SymbolTable {
    private final SymbolTable outer;
    private final Map<String, Symbol> store;
    private int numDefinitions;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable outer) {
        this.outer = outer;
        this.store = new HashMap<>();
        this.numDefinitions = 0;
    }

    /**
     * Chapter 7: 創建封閉的符號表 (用於函數作用域)
     */
    public static SymbolTable newEnclosed(SymbolTable outer) {
        return new SymbolTable(outer);
    }

    /**
     * 定義符號
     */
    public Symbol define(String name) {
        Symbol symbol;
        if (outer == null) {
            // 全局作用域
            symbol = new Symbol(name, SymbolScope.GLOBAL, numDefinitions);
        } else {
            // 局部作用域 (Chapter 7)
            symbol = new Symbol(name, SymbolScope.LOCAL, numDefinitions);
        }

        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    /**
     * 解析符號
     */
    public Symbol resolve(String name) {
        Symbol symbol = store.get(name);

        if (symbol == null && outer != null) {
            // 在外層作用域中查找 (Chapter 7)
            symbol = outer.resolve(name);
        }

        return symbol;
    }

    public int getNumDefinitions() {
        return numDefinitions;
    }

    public SymbolTable getOuter() {
        return outer;
    }
}
package com.monkey.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable 符號表
 * Chapter 8: Built-in Functions (擴展)
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

    public static SymbolTable newEnclosed(SymbolTable outer) {
        return new SymbolTable(outer);
    }

    public Symbol define(String name) {
        Symbol symbol;
        if (outer == null) {
            symbol = new Symbol(name, SymbolScope.GLOBAL, numDefinitions);
        } else {
            symbol = new Symbol(name, SymbolScope.LOCAL, numDefinitions);
        }

        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    /**
     * Chapter 8: 定義內建函數
     */
    public Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, SymbolScope.BUILTIN, index);
        store.put(name, symbol);
        return symbol;
    }

    public Symbol resolve(String name) {
        Symbol symbol = store.get(name);

        if (symbol == null && outer != null) {
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
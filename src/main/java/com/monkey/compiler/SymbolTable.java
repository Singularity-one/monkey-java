package com.monkey.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SymbolTable 符號表
 * Chapter 9: Closures (擴展)
 */
public class SymbolTable {
    private final SymbolTable outer;
    private final Map<String, Symbol> store;
    private int numDefinitions;

    // Chapter 9: 自由變量列表
    private final List<Symbol> freeSymbols;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable outer) {
        this.outer = outer;
        this.store = new HashMap<>();
        this.numDefinitions = 0;
        this.freeSymbols = new ArrayList<>();
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

    public Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, SymbolScope.BUILTIN, index);
        store.put(name, symbol);
        return symbol;
    }

    /**
     * Chapter 9: 定義自由變量
     */
    public Symbol defineFree(Symbol original) {
        freeSymbols.add(original);

        Symbol symbol = new Symbol(original.getName(), SymbolScope.FREE, freeSymbols.size() - 1);
        store.put(original.getName(), symbol);

        return symbol;
    }

    /**
     * Chapter 9: 解析符號 (支持自由變量)
     */
    public Symbol resolve(String name) {
        Symbol symbol = store.get(name);

        if (symbol == null && outer != null) {
            symbol = outer.resolve(name);

            if (symbol == null) {
                return null;
            }

            // 如果是全局變量或內建函數，直接返回
            if (symbol.getScope() == SymbolScope.GLOBAL ||
                    symbol.getScope() == SymbolScope.BUILTIN) {
                return symbol;
            }

            // 否則，將其定義為自由變量
            return defineFree(symbol);
        }

        return symbol;
    }

    public int getNumDefinitions() {
        return numDefinitions;
    }

    public SymbolTable getOuter() {
        return outer;
    }

    /**
     * Chapter 9: 獲取自由變量列表
     */
    public List<Symbol> getFreeSymbols() {
        return freeSymbols;
    }
}
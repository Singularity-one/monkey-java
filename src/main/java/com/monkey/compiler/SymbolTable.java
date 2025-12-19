package com.monkey.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable 管理變量名稱和它們的符號信息
 * Chapter 5: Keeping Track of Names
 *
 * 符號表的職責:
 * 1. 記錄變量名稱和它們的索引
 * 2. 區分不同作用域的變量
 * 3. 支持變量查找和定義
 */
public class SymbolTable {
    private final SymbolTable outer;                 // 外層符號表 (用於嵌套作用域)
    private final Map<String, Symbol> store;         // 符號存儲
    private int numDefinitions;                      // 已定義的符號數量

    /**
     * 創建新的符號表
     */
    public SymbolTable() {
        this.outer = null;
        this.store = new HashMap<>();
        this.numDefinitions = 0;
    }

    /**
     * 創建嵌套的符號表
     * Chapter 7 會用到
     */
    public SymbolTable(SymbolTable outer) {
        this.outer = outer;
        this.store = new HashMap<>();
        this.numDefinitions = 0;
    }

    /**
     * 定義一個新符號
     *
     * @param name 變量名稱
     * @return 新創建的符號
     */
    public Symbol define(String name) {
        // Chapter 5: 所有變量都是全局的
        Symbol symbol = new Symbol(name, SymbolScope.GLOBAL, numDefinitions);
        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }

    /**
     * 解析符號 (查找變量)
     *
     * @param name 變量名稱
     * @return 找到的符號,如果不存在返回 null
     */
    public Symbol resolve(String name) {
        Symbol symbol = store.get(name);

        // 如果當前作用域找不到,嘗試在外層作用域查找
        if (symbol == null && outer != null) {
            return outer.resolve(name);
        }

        return symbol;
    }

    /**
     * 獲取已定義的符號數量
     */
    public int getNumDefinitions() {
        return numDefinitions;
    }

    /**
     * 獲取外層符號表
     */
    public SymbolTable getOuter() {
        return outer;
    }

    /**
     * 獲取符號存儲 (用於測試)
     */
    public Map<String, Symbol> getStore() {
        return store;
    }
}
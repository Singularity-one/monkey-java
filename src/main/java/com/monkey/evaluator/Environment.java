package com.monkey.evaluator;
import com.monkey.ast.*;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator 遍歷 AST 並對其求值
 * 實現 Tree-Walking Interpreter
 */
public class Environment {
    private final Map<String, MonkeyObject> store;
    private final Environment outer;

    public Environment() {
        this.store = new HashMap<>();
        this.outer = null;
    }

    public Environment(Environment outer) {
        this.store = new HashMap<>();
        this.outer = outer;
    }

    /**
     * 獲取變數值
     * 如果當前環境中找不到，則在外層環境中查找
     */
    public MonkeyObject get(String name) {
        MonkeyObject obj = store.get(name);
        if (obj == null && outer != null) {
            return outer.get(name);
        }
        return obj;
    }

    /**
     * 設置變數值
     */
    public MonkeyObject set(String name, MonkeyObject val) {
        store.put(name, val);
        return val;
    }

    /**
     * 創建一個新的封閉環境
     */
    public static Environment newEnclosedEnvironment(Environment outer) {
        return new Environment(outer);
    }
}

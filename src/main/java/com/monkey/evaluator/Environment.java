package com.monkey.evaluator;
import com.monkey.object.MonkeyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment 用於儲存變數綁定
 * 支援作用域鏈（通過 outer 指向外層環境）
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

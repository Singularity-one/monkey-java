package com.monkey.ast;
/**
 * Statement 代表一個語句節點
 * 語句不產生值
 */
public interface Statement extends Node {
    /**
     * 標記方法，用於區分語句和表達式
     */
    default void statementNode() {}
}

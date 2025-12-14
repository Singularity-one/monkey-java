package com.monkey.ast;
/**
 * Expression 代表一個表達式節點
 * 表達式會產生值
 */
public interface Expression extends Node {
    /**
     * 標記方法，用於區分語句和表達式
     */
    default void expressionNode() {}
}
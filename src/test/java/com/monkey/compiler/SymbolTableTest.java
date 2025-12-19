package com.monkey.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 符號表測試
 * Chapter 5: Keeping Track of Names
 */
public class SymbolTableTest {

    /**
     * 測試符號定義
     */
    @Test
    public void testDefine() {
        SymbolTable global = new SymbolTable();

        Symbol a = global.define("a");
        assertEquals("a", a.getName());
        assertEquals(SymbolScope.GLOBAL, a.getScope());
        assertEquals(0, a.getIndex());

        Symbol b = global.define("b");
        assertEquals("b", b.getName());
        assertEquals(SymbolScope.GLOBAL, b.getScope());
        assertEquals(1, b.getIndex());
    }

    /**
     * 測試符號解析
     */
    @Test
    public void testResolveGlobal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        Symbol[] expected = {
                new Symbol("a", SymbolScope.GLOBAL, 0),
                new Symbol("b", SymbolScope.GLOBAL, 1)
        };

        for (Symbol exp : expected) {
            Symbol result = global.resolve(exp.getName());
            assertNotNull(exp.getName());
            assertNotNull(result);
            assertEquals(exp, result);
        }
    }

    /**
     * 測試解析未定義的符號
     */
    @Test
    public void testResolveUndefined() {
        SymbolTable global = new SymbolTable();
        global.define("a");

        Symbol result = global.resolve("b");  // "b" 未定義
        assertNull(result);  // 應該返回 null!
    }
}
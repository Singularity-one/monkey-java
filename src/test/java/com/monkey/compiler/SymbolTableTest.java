package com.monkey.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 符號表測試
 * Chapter 7: Functions (擴展)
 */
public class SymbolTableTest {

    @Test
    public void testDefine() {
        Symbol[] expected = {
                new Symbol("a", SymbolScope.GLOBAL, 0),
                new Symbol("b", SymbolScope.GLOBAL, 1),
                new Symbol("c", SymbolScope.LOCAL, 0),
                new Symbol("d", SymbolScope.LOCAL, 1),
                new Symbol("e", SymbolScope.LOCAL, 0),
                new Symbol("f", SymbolScope.LOCAL, 1)
        };

        SymbolTable global = new SymbolTable();

        Symbol a = global.define("a");
        assertEquals(expected[0], a);

        Symbol b = global.define("b");
        assertEquals(expected[1], b);

        SymbolTable firstLocal = SymbolTable.newEnclosed(global);

        Symbol c = firstLocal.define("c");
        assertEquals(expected[2], c);

        Symbol d = firstLocal.define("d");
        assertEquals(expected[3], d);

        SymbolTable secondLocal = SymbolTable.newEnclosed(firstLocal);

        Symbol e = secondLocal.define("e");
        assertEquals(expected[4], e);

        Symbol f = secondLocal.define("f");
        assertEquals(expected[5], f);
    }

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
            assertNotNull(result, exp.getName() + " not resolvable");
            assertEquals(exp, result);
        }
    }

    /**
     * Chapter 7: 測試局部作用域解析
     */
    @Test
    public void testResolveLocal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable local = SymbolTable.newEnclosed(global);
        local.define("c");
        local.define("d");

        Symbol[] expected = {
                new Symbol("a", SymbolScope.GLOBAL, 0),
                new Symbol("b", SymbolScope.GLOBAL, 1),
                new Symbol("c", SymbolScope.LOCAL, 0),
                new Symbol("d", SymbolScope.LOCAL, 1)
        };

        for (Symbol exp : expected) {
            Symbol result = local.resolve(exp.getName());
            assertNotNull(result, exp.getName() + " not resolvable");
            assertEquals(exp, result);
        }
    }

    /**
     * Chapter 7: 測試嵌套局部作用域解析
     */
    @Test
    public void testResolveNestedLocal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable firstLocal = SymbolTable.newEnclosed(global);
        firstLocal.define("c");
        firstLocal.define("d");

        SymbolTable secondLocal = SymbolTable.newEnclosed(firstLocal);
        secondLocal.define("e");
        secondLocal.define("f");

        Symbol[][] tests = {
                {
                        new Symbol("a", SymbolScope.GLOBAL, 0),
                        new Symbol("b", SymbolScope.GLOBAL, 1),
                        new Symbol("c", SymbolScope.LOCAL, 0),
                        new Symbol("d", SymbolScope.LOCAL, 1)
                },
                {
                        new Symbol("a", SymbolScope.GLOBAL, 0),
                        new Symbol("b", SymbolScope.GLOBAL, 1),
                        new Symbol("e", SymbolScope.LOCAL, 0),
                        new Symbol("f", SymbolScope.LOCAL, 1)
                }
        };

        SymbolTable[] tables = {firstLocal, secondLocal};

        for (int i = 0; i < tables.length; i++) {
            for (Symbol exp : tests[i]) {
                Symbol result = tables[i].resolve(exp.getName());
                assertNotNull(result, exp.getName() + " not resolvable");
                assertEquals(exp, result,
                        String.format("expected %s to resolve to %s, got=%s",
                                exp.getName(), exp, result));
            }
        }
    }

    /**
     * Chapter 8: 測試內建函數的定義和解析
     */
    @Test
    public void testDefineResolveBuiltins() {
        SymbolTable global = new SymbolTable();
        SymbolTable firstLocal = SymbolTable.newEnclosed(global);
        SymbolTable secondLocal = SymbolTable.newEnclosed(firstLocal);

        Symbol[] expected = {
                new Symbol("a", SymbolScope.BUILTIN, 0),
                new Symbol("c", SymbolScope.BUILTIN, 1),
                new Symbol("e", SymbolScope.BUILTIN, 2),
                new Symbol("f", SymbolScope.BUILTIN, 3)
        };

        for (int i = 0; i < expected.length; i++) {
            global.defineBuiltin(i, expected[i].getName());
        }

        SymbolTable[] tables = {global, firstLocal, secondLocal};

        for (SymbolTable table : tables) {
            for (Symbol exp : expected) {
                Symbol result = table.resolve(exp.getName());
                assertNotNull(result, exp.getName() + " not resolvable");
                assertEquals(exp, result,
                        String.format("expected %s to resolve to %s, got=%s",
                                exp.getName(), exp, result));
            }
        }
    }

    @Test
    public void testResolveUndefined() {
        SymbolTable global = new SymbolTable();
        global.define("a");

        Symbol result = global.resolve("b");
        assertNull(result);
    }
}
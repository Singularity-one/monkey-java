package com.monkey.compiler;

import com.monkey.ast.Program;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 編譯器測試
 * Chapter 7: Functions (擴展)
 */
public class CompilerTest {

    @Test
    public void testIntegerArithmetic() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "1 + 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "true",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testConditionals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "if (true) { 10 }; 3333;",
                        new Object[]{10, 3333},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_JUMP_NOT_TRUTHY, 10),
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_JUMP, 11),
                                Instructions.make(Opcode.OP_NULL),
                                Instructions.make(Opcode.OP_POP),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testGlobalLetStatements() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "let one = 1; let two = 2;",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 1)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testStringExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "\"monkey\"",
                        new Object[]{"monkey"},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "\"mon\" + \"key\"",
                        new Object[]{"mon", "key"},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testArrayLiterals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "[]",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_ARRAY, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "[1, 2, 3]",
                        new Object[]{1, 2, 3},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_ARRAY, 3),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testHashLiterals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "{}",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_HASH, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "{1: 2, 3: 4, 5: 6}",
                        new Object[]{1, 2, 3, 4, 5, 6},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_CONSTANT, 5),
                                Instructions.make(Opcode.OP_HASH, 6),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testIndexExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "[1, 2, 3][1 + 1]",
                        new Object[]{1, 2, 3, 1, 1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_ARRAY, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_INDEX),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試函數字面量編譯
     */
    @Test
    public void testFunctions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "fn() { return 5 + 10 }",
                        new Object[]{
                                5,
                                10,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_CONSTANT, 1),
                                        Instructions.make(Opcode.OP_ADD),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn() { 5 + 10 }",
                        new Object[]{
                                5,
                                10,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_CONSTANT, 1),
                                        Instructions.make(Opcode.OP_ADD),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn() { 1; 2 }",
                        new Object[]{
                                1,
                                2,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_POP),
                                        Instructions.make(Opcode.OP_CONSTANT, 1),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試無返回值函數
     */
    @Test
    public void testFunctionsWithoutReturnValue() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "fn() { }",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_RETURN)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試函數調用
     */
    @Test
    public void testFunctionCalls() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "fn() { 24 }();",
                        new Object[]{
                                24,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CALL, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "let noArg = fn() { 24 }; noArg();",
                        new Object[]{
                                24,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CALL, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試局部作用域 let 語句
     */
    @Test
    public void testLetStatementScopes() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "let num = 55; fn() { num }",
                        new Object[]{
                                55,
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn() { let num = 55; num }",
                        new Object[]{
                                55,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_SET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn() { let a = 55; let b = 77; a + b }",
                        new Object[]{
                                55,
                                77,
                                new Object[]{
                                        Instructions.make(Opcode.OP_CONSTANT, 0),
                                        Instructions.make(Opcode.OP_SET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_CONSTANT, 1),
                                        Instructions.make(Opcode.OP_SET_LOCAL, 1),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 1),
                                        Instructions.make(Opcode.OP_ADD),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試函數參數
     */
    @Test
    public void testFunctionsWithArguments() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "fn(a) { a }",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn(a, b, c) { a; b; c }",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_POP),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 1),
                                        Instructions.make(Opcode.OP_POP),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 2),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 7: 測試帶參數的函數調用
     */
    @Test
    public void testFunctionCallsWithArguments() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "fn(a) { a }(24);",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                },
                                24
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CALL, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "let oneArg = fn(a) { a }; oneArg(24);",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                },
                                24
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CALL, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "let manyArg = fn(a, b, c) { a; b; c }; manyArg(24, 25, 26);",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_LOCAL, 0),
                                        Instructions.make(Opcode.OP_POP),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 1),
                                        Instructions.make(Opcode.OP_POP),
                                        Instructions.make(Opcode.OP_GET_LOCAL, 2),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                },
                                24,
                                25,
                                26
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CALL, 3),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 8: 測試內建函數編譯
     */
    @Test
    public void testBuiltins() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "len([]); push([], 1);",
                        new Object[]{1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_GET_BUILTIN, 0),
                                Instructions.make(Opcode.OP_ARRAY, 0),
                                Instructions.make(Opcode.OP_CALL, 1),
                                Instructions.make(Opcode.OP_POP),
                                Instructions.make(Opcode.OP_GET_BUILTIN, 5),
                                Instructions.make(Opcode.OP_ARRAY, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CALL, 2),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "fn() { len([]) }",
                        new Object[]{
                                new Object[]{
                                        Instructions.make(Opcode.OP_GET_BUILTIN, 0),
                                        Instructions.make(Opcode.OP_ARRAY, 0),
                                        Instructions.make(Opcode.OP_CALL, 1),
                                        Instructions.make(Opcode.OP_RETURN_VALUE)
                                }
                        },
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    private void runCompilerTests(CompilerTestCase[] tests) {
        for (CompilerTestCase tt : tests) {
            Program program = parse(tt.input);

            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            Bytecode bytecode = compiler.bytecode();

            testInstructions(tt.expectedInstructions, bytecode.getInstructions());
            testConstants(tt.expectedConstants, bytecode.getConstants());
        }
    }

    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

    private void testInstructions(byte[][] expected, Instructions actual) {
        Instructions concatenated = concatInstructions(expected);

        assertEquals(concatenated.size(), actual.size(),
                String.format("wrong instructions length.\nwant=%s\ngot=%s",
                        concatenated, actual));

        for (int i = 0; i < concatenated.size(); i++) {
            assertEquals(concatenated.get(i), actual.get(i),
                    String.format("wrong instruction at %d.\nwant=%s\ngot=%s",
                            i, concatenated, actual));
        }
    }

    private Instructions concatInstructions(byte[][] s) {
        Instructions out = new Instructions();
        for (byte[] ins : s) {
            out.append(ins);
        }
        return out;
    }

    private void testConstants(Object[] expected, List<MonkeyObject> actual) {
        assertEquals(expected.length, actual.size(),
                "wrong number of constants");

        for (int i = 0; i < expected.length; i++) {
            Object constant = expected[i];

            if (constant instanceof Integer) {
                testIntegerObject((long) (int) constant, actual.get(i));
            }
            else if (constant instanceof String) {
                testStringObject((String) constant, actual.get(i));
            }
            else if (constant instanceof Object[]) {
                // Chapter 7: 處理 CompiledFunction 的指令
                assertTrue(actual.get(i) instanceof CompiledFunctionObject,
                        "constant " + i + " is not a CompiledFunction. got=" + actual.get(i).getClass());

                CompiledFunctionObject fn = (CompiledFunctionObject) actual.get(i);
                Object[] instructionsArray = (Object[]) constant;

                // 將 Object[] 中的 byte[] 連接成完整的指令序列
                List<byte[]> expectedInstructions = new ArrayList<>();
                for (Object obj : instructionsArray) {
                    if (obj instanceof byte[]) {
                        expectedInstructions.add((byte[]) obj);
                    }
                }

                // 使用現有的 testInstructions 方法
                byte[][] insArray = expectedInstructions.toArray(new byte[0][]);
                testInstructions(insArray, fn.getInstructions());
            }
        }
    }

    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value");
    }

    private void testStringObject(String expected, MonkeyObject actual) {
        assertTrue(actual instanceof StringObject,
                "object is not String. got=" + actual.getClass());

        StringObject result = (StringObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value");
    }

    private static class CompilerTestCase {
        String input;
        Object[] expectedConstants;
        byte[][] expectedInstructions;

        CompilerTestCase(String input, Object[] expectedConstants, byte[][] expectedInstructions) {
            this.input = input;
            this.expectedConstants = expectedConstants;
            this.expectedInstructions = expectedInstructions;
        }
    }

}
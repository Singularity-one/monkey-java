package com.monkey.benchmark;

import com.monkey.ast.Program;
import com.monkey.compiler.Bytecode;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import com.monkey.vm.VM;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Chapter 10: 性能基準測試
 * 比較編譯器+VM的性能
 */
@Tag("benchmark")
public class BenchmarkTest {

    /**
     * 斐波那契基準測試 - 小數值
     */
    @Test
    @DisplayName("Fibonacci(15) - Quick Test")
    public void testFibonacci15() {
        String input = """
                let fibonacci = fn(x) {
                    if (x == 0) {
                        return 0;
                    } else {
                        if (x == 1) {
                            return 1;
                        } else {
                            fibonacci(x - 1) + fibonacci(x - 2);
                        }
                    }
                };
                fibonacci(15);
                """;

        BenchmarkResult result = runBenchmark(input, "fibonacci(15)");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Fibonacci(15) Benchmark Result");
        System.out.println("=".repeat(60));
        System.out.println(result);
        System.out.println("=".repeat(60) + "\n");

        assertEquals(610L, result.getResultValue(), "fibonacci(15) should equal 610");
    }

    /**
     * 斐波那契基準測試 - 中等數值
     */
    @Test
    @DisplayName("Fibonacci(20) - Medium Test")
    public void testFibonacci20() {
        String input = """
                let fibonacci = fn(x) {
                    if (x == 0) {
                        return 0;
                    } else {
                        if (x == 1) {
                            return 1;
                        } else {
                            fibonacci(x - 1) + fibonacci(x - 2);
                        }
                    }
                };
                fibonacci(20);
                """;

        BenchmarkResult result = runBenchmark(input, "fibonacci(20)");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Fibonacci(20) Benchmark Result");
        System.out.println("=".repeat(60));
        System.out.println(result);
        System.out.println("=".repeat(60) + "\n");

        assertEquals(6765L, result.getResultValue(), "fibonacci(20) should equal 6765");
    }

    /**
     * 斐波那契基準測試 - 較大數值（需要更長時間）
     */
    @Test
    @DisplayName("Fibonacci(25) - Intensive Test")
    public void testFibonacci25() {
        String input = """
                let fibonacci = fn(x) {
                    if (x == 0) {
                        return 0;
                    } else {
                        if (x == 1) {
                            return 1;
                        } else {
                            fibonacci(x - 1) + fibonacci(x - 2);
                        }
                    }
                };
                fibonacci(25);
                """;

        BenchmarkResult result = runBenchmark(input, "fibonacci(25)");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Fibonacci(25) Benchmark Result");
        System.out.println("=".repeat(60));
        System.out.println(result);
        System.out.println("=".repeat(60) + "\n");

        assertEquals(75025L, result.getResultValue(), "fibonacci(25) should equal 75025");
    }

    /**
     * 多次運行基準測試以獲得平均值
     */
    @Test
    @DisplayName("Fibonacci(15) - Multiple Runs Average")
    public void testFibonacciMultipleRuns() {
        String input = """
                let fibonacci = fn(x) {
                    if (x == 0) {
                        return 0;
                    } else {
                        if (x == 1) {
                            return 1;
                        } else {
                            fibonacci(x - 1) + fibonacci(x - 2);
                        }
                    }
                };
                fibonacci(15);
                """;

        int runs = 5;
        long totalDuration = 0;
        long totalCompileTime = 0;
        long totalExecutionTime = 0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Running Fibonacci(15) " + runs + " times...");
        System.out.println("=".repeat(60));

        for (int i = 0; i < runs; i++) {
            BenchmarkResult result = runBenchmark(input, "fibonacci(15) run#" + (i + 1));
            totalDuration += result.getTotalDuration();
            totalCompileTime += result.getCompileTime();
            totalExecutionTime += result.getExecutionTime();

            System.out.printf("Run #%d: Total=%dms, Compile=%dms, Execute=%dms%n",
                    i + 1,
                    result.getTotalDuration(),
                    result.getCompileTime(),
                    result.getExecutionTime());
        }

        System.out.println("-".repeat(60));
        System.out.printf("Average: Total=%dms, Compile=%dms, Execute=%dms%n",
                totalDuration / runs,
                totalCompileTime / runs,
                totalExecutionTime / runs);
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * 測試編譯時間 vs 執行時間的比例
     */
    @Test
    @DisplayName("Compile vs Execute Time Ratio")
    public void testCompileVsExecuteRatio() {
        String input = """
                let fibonacci = fn(x) {
                    if (x == 0) {
                        return 0;
                    } else {
                        if (x == 1) {
                            return 1;
                        } else {
                            fibonacci(x - 1) + fibonacci(x - 2);
                        }
                    }
                };
                fibonacci(15);
                """;

        BenchmarkResult result = runBenchmark(input, "fibonacci(15)");

        double compilePercent = (double) result.getCompileTime() / result.getTotalDuration() * 100;
        double executePercent = (double) result.getExecutionTime() / result.getTotalDuration() * 100;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Compile vs Execute Time Analysis");
        System.out.println("=".repeat(60));
        System.out.printf("Total Duration:    %d ms (100%%)%n", result.getTotalDuration());
        System.out.printf("Compile Time:      %d ms (%.2f%%)%n", result.getCompileTime(), compilePercent);
        System.out.printf("Execute Time:      %d ms (%.2f%%)%n", result.getExecutionTime(), executePercent);
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * 測試數組操作性能
     */
    @Test
    @DisplayName("Array Operations Benchmark")
    public void testArrayOperations() {
        String input = """
            let arr = [1, 2, 3, 4, 5];
            let double = fn(x) { x * 2 };
            
            let a = double(first(arr));
            let b = double(last(arr));
            a + b;
            """;

        BenchmarkResult result = runBenchmark(input, "array operations");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Array Operations Benchmark Result");
        System.out.println("=".repeat(60));
        System.out.println(result);
        System.out.println("=".repeat(60) + "\n");

        assertEquals(12L, result.getResultValue()); // 2 + 10
    }

    /**
     * 執行基準測試
     */
    private BenchmarkResult runBenchmark(String input, String description) {
        // 解析代碼
        long parseStart = System.nanoTime();
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        long parseDuration = System.nanoTime() - parseStart;

        assertNotNull(program, "Program should not be null");
        assertEquals(0, parser.getErrors().size(),
                "Parser should have no errors: " + parser.getErrors());

        // 編譯代碼
        long compileStart = System.nanoTime();
        Compiler compiler = new Compiler();
        try {
            compiler.compile(program);
        } catch (Compiler.CompilerException e) {
            fail("Compilation failed: " + e.getMessage());
        }
        Bytecode bytecode = compiler.bytecode();
        long compileDuration = System.nanoTime() - compileStart;

        // 執行代碼
        long executeStart = System.nanoTime();
        VM vm = new VM(bytecode);
        try {
            vm.run();
        } catch (VM.VMException e) {
            fail("VM execution failed: " + e.getMessage());
        }
        long executeDuration = System.nanoTime() - executeStart;

        MonkeyObject result = vm.lastPoppedStackElem();
        assertNotNull(result, "Result should not be null");

        return new BenchmarkResult(
                description,
                result,
                parseDuration / 1_000_000,      // 轉換為毫秒
                compileDuration / 1_000_000,    // 轉換為毫秒
                executeDuration / 1_000_000     // 轉換為毫秒
        );
    }

    /**
     * 基準測試結果類
     */
    private static class BenchmarkResult {
        private final String description;
        private final MonkeyObject result;
        private final long parseTime;      // 毫秒
        private final long compileTime;    // 毫秒
        private final long executionTime;  // 毫秒

        public BenchmarkResult(String description, MonkeyObject result,
                               long parseTime, long compileTime, long executionTime) {
            this.description = description;
            this.result = result;
            this.parseTime = parseTime;
            this.compileTime = compileTime;
            this.executionTime = executionTime;
        }

        public long getTotalDuration() {
            return parseTime + compileTime + executionTime;
        }

        public long getCompileTime() {
            return compileTime;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public long getResultValue() {
            if (result instanceof com.monkey.object.IntegerObject) {
                return ((com.monkey.object.IntegerObject) result).getValue();
            }
            return -1;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Description:       ").append(description).append("\n");
            sb.append("Result:            ").append(result.inspect()).append("\n");
            sb.append("Parse Time:        ").append(parseTime).append(" ms\n");
            sb.append("Compile Time:      ").append(compileTime).append(" ms\n");
            sb.append("Execution Time:    ").append(executionTime).append(" ms\n");
            sb.append("Total Time:        ").append(getTotalDuration()).append(" ms");
            return sb.toString();
        }
    }
}
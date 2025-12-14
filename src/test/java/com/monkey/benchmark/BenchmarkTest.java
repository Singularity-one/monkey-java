package com.monkey.benchmark;
import com.monkey.ast.Program;
import com.monkey.compiler.Compiler;
import com.monkey.evaluator.Evaluator;
import com.monkey.lexer.Lexer;
import com.monkey.evaluator.Environment;
import com.monkey.parser.Parser;
import com.monkey.vm.VM;

/**
 * 性能對比測試：編譯器 vs 解釋器
 */
public class BenchmarkTest {

    public static void main(String[] args) throws Exception {
        String[] testCases = {
                "1 + 2",
                "1 + 2 + 3 + 4 + 5",
                "1 * 2 * 3 * 4 * 5",
                "(1 + 2) * (3 + 4)",
                "5 * (2 + 10)",

                // 更複雜的運算
                """
            let fibonacci = fn(x) {
                if (x == 0) {
                    0
                } else {
                    if (x == 1) {
                        1
                    } else {
                        fibonacci(x - 1) + fibonacci(x - 2)
                    }
                }
            };
            fibonacci(15);
            """,

                // 大量運算
                "(1 + 2) * (3 + 4) * (5 + 6) * (7 + 8)"
        };

        System.out.println("=".repeat(70));
        System.out.println("Performance Benchmark: Compiler vs Interpreter");
        System.out.println("=".repeat(70));

        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            System.out.println("\nTest Case " + (i + 1) + ":");
            System.out.println(input.length() > 60
                    ? input.substring(0, 57) + "..."
                    : input);
            System.out.println("-".repeat(70));

            // 預熱
            runInterpreter(input);
            runCompiler(input);

            // 測試解釋器
            long interpreterStart = System.nanoTime();
            for (int j = 0; j < 10000; j++) {
                runInterpreter(input);
            }
            long interpreterTime = System.nanoTime() - interpreterStart;

            // 測試編譯器
            long compilerStart = System.nanoTime();
            for (int j = 0; j < 10000; j++) {
                runCompiler(input);
            }
            long compilerTime = System.nanoTime() - compilerStart;

            // 輸出結果
            System.out.printf("Interpreter: %,d ns (%.2f ms)%n",
                    interpreterTime, interpreterTime / 1_000_000.0);
            System.out.printf("Compiler:    %,d ns (%.2f ms)%n",
                    compilerTime, compilerTime / 1_000_000.0);

            double speedup = (double) interpreterTime / compilerTime;
            System.out.printf("Speedup:     %.2fx faster%n", speedup);

            if (speedup > 1) {
                System.out.println("✅ Compiler is faster!");
            } else {
                System.out.println("⚠️  Interpreter is faster (unexpected)");
            }
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("Benchmark Complete!");
        System.out.println("=".repeat(70));
    }

    private static void runInterpreter(String input) {
        try {
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            Environment env = new Environment();
            Evaluator.eval(program, env);
        } catch (Exception e) {
            // 忽略錯誤
        }
    }

    private static void runCompiler(String input) {
        try {
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            Compiler compiler = new Compiler();
            compiler.compile(program);

            VM vm = new VM(compiler.bytecode());
            vm.run();
        } catch (Exception e) {
            // 忽略錯誤
        }
    }
}
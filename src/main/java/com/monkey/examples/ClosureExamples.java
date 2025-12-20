package com.monkey.examples;

import com.monkey.ast.Program;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import com.monkey.vm.VM;

/**
 * Chapter 9: 閉包示例
 */
public class ClosureExamples {

    public static void main(String[] args) {
        // 示例 1: 簡單閉包
        System.out.println("=== 示例 1: 簡單閉包 ===");
        runExample("""
                let newAdder = fn(a) {
                    fn(b) { a + b };
                };
                let addTwo = newAdder(2);
                addTwo(3);
                """);

        // 示例 2: 嵌套閉包
        System.out.println("\n=== 示例 2: 嵌套閉包 ===");
        runExample("""
                let newAdder = fn(a) {
                    fn(b) {
                        fn(c) { a + b + c };
                    };
                };
                let addTwo = newAdder(2);
                let addTwoAndThree = addTwo(3);
                addTwoAndThree(4);
                """);

        // 示例 3: 閉包與全局變量
        System.out.println("\n=== 示例 3: 閉包與全局變量 ===");
        runExample("""
                let global = 10;
                let makeAdder = fn(a) {
                    fn(b) { global + a + b };
                };
                let adder = makeAdder(5);
                adder(3);
                """);

        // 示例 4: 計數器閉包
        System.out.println("\n=== 示例 4: 計數器閉包 ===");
        runExample("""
                let newCounter = fn() {
                    let count = 0;
                    fn() {
                        let count = count + 1;
                        count
                    };
                };
                let counter = newCounter();
                counter();
                """);

        // 示例 5: 使用內建函數的閉包
        System.out.println("\n=== 示例 5: 使用內建函數的閉包 ===");
        runExample("""
                let map = fn(arr, f) {
                    let iter = fn(arr, accumulated) {
                        if (len(arr) == 0) {
                            accumulated
                        } else {
                            iter(rest(arr), push(accumulated, f(first(arr))));
                        }
                    };
                    iter(arr, []);
                };
                
                let double = fn(x) { x * 2 };
                map([1, 2, 3, 4], double);
                """);

        // 示例 6: 遞歸閉包
        System.out.println("\n=== 示例 6: 遞歸閉包 ===");
        runExample("""
                let fibonacci = fn(n) {
                    if (n < 2) {
                        n
                    } else {
                        fibonacci(n - 1) + fibonacci(n - 2)
                    }
                };
                fibonacci(10);
                """);
    }

    private static void runExample(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        if (parser.getErrors().size() > 0) {
            System.err.println("Parser errors:");
            for (String error : parser.getErrors()) {
                System.err.println("\t" + error);
            }
            return;
        }

        Compiler compiler = new Compiler();
        try {
            compiler.compile(program);
        } catch (Compiler.CompilerException e) {
            System.err.println("Compilation error: " + e.getMessage());
            return;
        }

        VM vm = new VM(compiler.bytecode());
        try {
            vm.run();
        } catch (VM.VMException e) {
            System.err.println("VM error: " + e.getMessage());
            return;
        }

        MonkeyObject result = vm.lastPoppedStackElem();
        System.out.println("結果: " + result.inspect());
    }
}
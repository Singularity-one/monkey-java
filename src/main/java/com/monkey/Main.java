package com.monkey;

import com.monkey.ast.Program;
import com.monkey.compiler.Bytecode;
import com.monkey.compiler.Compiler;
import com.monkey.evaluator.Evaluator;
import com.monkey.lexer.Lexer;
import com.monkey.evaluator.Environment;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import com.monkey.vm.VM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * Monkey 語言主程序
 * Chapter 2: 整合編譯器和虛擬機
 *
 * 支持兩種執行模式:
 * 1. 解釋器模式 (Tree-Walking Interpreter)
 * 2. 編譯器模式 (Bytecode Compiler + VM)
 */
public class Main {
    private static final String PROMPT = ">> ";
    private static final String MONKEY_FACE = """
              __,__
     .--.  .-"     "-.  .--.
    / .. \\/  .-. .-.  \\/ .. \\
   | |  '|  /   Y   \\  |'  | |
   | \\   \\  \\ 0 | 0 /  /   / |
    \\ '- ,\\.-"`` ``"-./, -' /
     `'-' /_   ^ ^   _\\ '-'`
         |  \\._   _./  |
         \\   \\ `~` /   /
          '._ '-=-' _.'
             '~---~'
    """;

    public static void main(String[] args) {
        System.out.println("Hello! This is the Monkey programming language!");
        System.out.println("Feel free to type in commands");
        System.out.println(MONKEY_FACE);

        // 檢查命令行參數
        if (args.length > 0) {
            switch (args[0]) {
                case "--demo":
                    runDemo();
                    return;
                case "--interpreter":
                    startInterpreterREPL();
                    return;
                case "--compiler":
                    startCompilerREPL();
                    return;
                case "--help":
                    printHelp();
                    return;
            }
        }

        // 默認使用編譯器模式
        System.out.println("Mode: Compiler (use --interpreter for interpreter mode)\n");
        startCompilerREPL();
    }

    /**
     * 打印幫助信息
     */
    private static void printHelp() {
        System.out.println("Monkey Language - Usage:");
        System.out.println("  java -jar monkey.jar              Start compiler REPL (default)");
        System.out.println("  java -jar monkey.jar --compiler   Start compiler REPL");
        System.out.println("  java -jar monkey.jar --interpreter Start interpreter REPL");
        System.out.println("  java -jar monkey.jar --demo       Run demo examples");
        System.out.println("  java -jar monkey.jar --help       Show this help");
    }

    /**
     * 啟動編譯器 REPL
     */
    private static void startCompilerREPL() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out, true);

        while (true) {
            writer.print(PROMPT);
            writer.flush();

            String line;
            try {
                line = reader.readLine();
                if (line == null || line.equals("exit")) {
                    writer.println("Goodbye!");
                    return;
                }
            } catch (IOException e) {
                return;
            }

            // 詞法分析
            Lexer lexer = new Lexer(line);

            // 語法分析
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            if (parser.getErrors().size() != 0) {
                printParserErrors(writer, parser.getErrors());
                continue;
            }

            // 編譯
            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);
            } catch (Compiler.CompilerException e) {
                writer.println("Woops! Compilation failed:");
                writer.println(" " + e.getMessage());
                continue;
            }

            // 執行
            VM machine = new VM(compiler.bytecode());
            try {
                machine.run();
            } catch (VM.VMException e) {
                writer.println("Woops! Executing bytecode failed:");
                writer.println(" " + e.getMessage());
                continue;
            }

            // 輸出結果
            MonkeyObject stackTop = machine.stackTop();
            if (stackTop != null) {
                writer.println(stackTop.inspect());
            }
        }
    }

    /**
     * 啟動解釋器 REPL
     */
    private static void startInterpreterREPL() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out, true);
        Environment env = new Environment();

        while (true) {
            writer.print(PROMPT);
            writer.flush();

            String line;
            try {
                line = reader.readLine();
                if (line == null || line.equals("exit")) {
                    writer.println("Goodbye!");
                    return;
                }
            } catch (IOException e) {
                return;
            }

            Lexer lexer = new Lexer(line);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            if (parser.getErrors().size() != 0) {
                printParserErrors(writer, parser.getErrors());
                continue;
            }

            MonkeyObject evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                writer.println(evaluated.inspect());
            }
        }
    }

    /**
     * 打印解析器錯誤
     */
    private static void printParserErrors(PrintWriter writer, List<String> errors) {
        writer.println(MONKEY_FACE);
        writer.println("Woops! We ran into some monkey business here!");
        writer.println(" parser errors:");
        for (String msg : errors) {
            writer.println("\t" + msg);
        }
    }

    /**
     * 運行演示程序
     */
    private static void runDemo() {
        System.out.println("=== Monkey Compiler & VM Demo ===\n");

        String[] tests = {
                "1",
                "2",
                "1 + 2",
                "1 - 2",
                "1 * 2",
                "4 / 2",
                "50 / 2 * 2 + 10",
                "2 * (5 + 10)",
                "3 * 3 * 3 + 10",
                "3 * (3 * 3) + 10",
                "(5 + 10 * 2 + 15 / 3) * 2 + -10"
        };

        for (String input : tests) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Input: " + input);

            // 解析
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            if (!parser.getErrors().isEmpty()) {
                System.out.println("Parser errors:");
                parser.getErrors().forEach(System.out::println);
                continue;
            }

            // 編譯
            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);
            } catch (Compiler.CompilerException e) {
                System.out.println("Compiler error: " + e.getMessage());
                continue;
            }

            Bytecode bytecode = compiler.bytecode();

            // 顯示編譯結果
            System.out.println("\nConstants:");
            List<MonkeyObject> constants = bytecode.getConstants();
            for (int i = 0; i < constants.size(); i++) {
                System.out.printf("  %d: %s\n", i, constants.get(i).inspect());
            }

            System.out.println("\nInstructions:");
            System.out.print(bytecode.getInstructions());

            // 執行
            VM vm = new VM(bytecode);
            try {
                vm.run();
            } catch (VM.VMException e) {
                System.out.println("VM error: " + e.getMessage());
                continue;
            }

            MonkeyObject result = vm.stackTop();
            System.out.println("Result: " + (result != null ? result.inspect() : "null"));
            System.out.println();
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Demo completed!");
    }
}
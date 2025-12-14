package com.monkey;

import com.monkey.ast.Program;
import com.monkey.evaluator.Environment;
import com.monkey.evaluator.Evaluator;
import com.monkey.lexer.Lexer;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import com.monkey.token.Token;
import com.monkey.token.TokenType;

import java.util.Scanner;

/**
 * Main 類用於測試 Lexer
 */
public class Main {

    //Chapter 3
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
// Chapter 1
//        String input = """
//            let five = 5;
//            let ten = 10;
//
//            let add = fn(x, y) {
//              x + y;
//            };
//
//            let result = add(five, ten);
//            !-/*5;
//            5 < 10 > 5;
//
//            if (5 < 10) {
//                return true;
//            } else {
//                return false;
//            }
//
//            10 == 10;
//            10 != 9;
//            """;
//
//        System.out.println("Lexing the following input:");
//        System.out.println("----------------------------");
//        System.out.println(input);
//        System.out.println("----------------------------");
//        System.out.println("\nTokens generated:");
//        System.out.println("----------------------------");
//
//        Lexer lexer = new Lexer(input);
//
//        Token tok = lexer.nextToken();
//        int tokenCount = 0;
//
//        while (tok.getType() != TokenType.EOF) {
//            System.out.printf("%3d. Type: %-12s Literal: '%s'%n",
//                    ++tokenCount,
//                    tok.getType(),
//                    tok.getLiteral());
//            tok = lexer.nextToken();
//        }
//
//        System.out.println("----------------------------");
//        System.out.println("Total tokens: " + tokenCount);
//        System.out.println("\n✅ Lexer 運行完成！");

        // Chapter 2
//        System.out.println("=".repeat(60));
//        System.out.println("Monkey Language Parser - Chapter 2 Demo");
//        System.out.println("=".repeat(60));
//
//        // 測試各種表達式和語句
//        String[] testCases = {
//                // Let 語句
//                "let x = 5;",
//                "let y = 10;",
//                "let foobar = 838383;",
//
//                // Return 語句
//                "return 5;",
//                "return x + y;",
//
//                // 表達式語句
//                "5;",
//                "foobar;",
//
//                // 前綴表達式
//                "-5;",
//                "!true;",
//                "!false;",
//
//                // 中綴表達式
//                "5 + 5;",
//                "5 - 5;",
//                "5 * 5;",
//                "5 / 5;",
//                "5 > 5;",
//                "5 < 5;",
//                "5 == 5;",
//                "5 != 5;",
//
//                // 複雜表達式（測試優先級）
//                "3 + 4 * 5;",
//                "(5 + 5) * 2;",
//                "2 / (5 + 5);",
//                "-(5 + 5);",
//
//                // 布林表達式
//                "true == true;",
//                "false != true;",
//                "3 > 5 == false;",
//
//                // If 表達式
//                "if (x < y) { x }",
//                "if (x < y) { x } else { y }",
//
//                // 函數字面值
//                "fn(x, y) { x + y; }",
//                "fn() { return 5; }",
//
//                // 函數調用
//                "add(1, 2);",
//                "add(1, 2 * 3, 4 + 5);",
//
//                // 複雜程式
//                """
//            let add = fn(x, y) {
//                x + y;
//            };
//
//            let result = add(5, 10);
//            result;
//            """
//        };
//
//        for (int i = 0; i < testCases.length; i++) {
//            String input = testCases[i];
//            System.out.println("\n" + "-".repeat(60));
//            System.out.printf("Test Case %d:%n", i + 1);
//            System.out.println("-".repeat(60));
//            System.out.println("Input:");
//            System.out.println(input);
//            System.out.println();
//
//            Lexer lexer = new Lexer(input);
//            Parser parser = new Parser(lexer);
//            Program program = parser.parseProgram();
//
//            if (!parser.getErrors().isEmpty()) {
//                System.err.println("❌ Parser Errors:");
//                for (String error : parser.getErrors()) {
//                    System.err.println("  - " + error);
//                }
//            } else {
//                System.out.println("✅ Parsed successfully!");
//                System.out.println("\nAST:");
//                System.out.println(program.string());
//
//                System.out.println("\nNumber of statements: " + program.getStatements().size());
//            }
//        }
//
//        System.out.println("\n" + "=".repeat(60));
//        System.out.println("🎉 Parser Demo Complete!");
//        System.out.println("=".repeat(60));
//
//        // 展示一個完整的 Monkey 程式
//        System.out.println("\nParsing a complete Monkey program:");
//        System.out.println("=".repeat(60));
//
//        String completeProgram = """
//            let five = 5;
//            let ten = 10;
//
//            let add = fn(x, y) {
//              x + y;
//            };
//
//            let result = add(five, ten);
//
//            if (result > 10) {
//                return true;
//            } else {
//                return false;
//            }
//            """;
//
//        System.out.println(completeProgram);
//        System.out.println("=".repeat(60));
//
//        Lexer lexer = new Lexer(completeProgram);
//        Parser parser = new Parser(lexer);
//        Program program = parser.parseProgram();
//
//        if (!parser.getErrors().isEmpty()) {
//            System.err.println("❌ Parser Errors:");
//            for (String error : parser.getErrors()) {
//                System.err.println("  - " + error);
//            }
//        } else {
//            System.out.println("✅ Complete program parsed successfully!");
//            System.out.println("\nGenerated AST:");
//            System.out.println(program.string());
//        }

        System.out.println("Hello! This is the Monkey programming language!");
        System.out.println("Feel free to type in commands");
        System.out.println(MONKEY_FACE);

        if (args.length > 0 && args[0].equals("--demo")) {
            runDemo();
        } else {
            startRepl();
        }
    }

    /**
     * 啟動 REPL
     */
    private static void startRepl() {
        Scanner scanner = new Scanner(System.in);
        Environment env = new Environment();

        while (true) {
            System.out.print(PROMPT);

            if (!scanner.hasNextLine()) {
                break;
            }

            String line = scanner.nextLine();

            if (line.equals("exit") || line.equals("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (line.trim().isEmpty()) {
                continue;
            }

            Lexer lexer = new Lexer(line);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            if (!parser.getErrors().isEmpty()) {
                printParserErrors(parser.getErrors());
                continue;
            }

            MonkeyObject evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                System.out.println(evaluated.inspect());
            }
        }

        scanner.close();
    }

    /**
     * 打印 Parser 錯誤
     */
    private static void printParserErrors(java.util.List<String> errors) {
        System.out.println(MONKEY_FACE);
        System.out.println("Woops! We ran into some monkey business here!");
        System.out.println(" parser errors:");
        for (String msg : errors) {
            System.out.println("\t" + msg);
        }
    }

    /**
     * 運行示範程式
     */
    private static void runDemo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Monkey Language Evaluator - Chapter 3 Demo");
        System.out.println("=".repeat(60));

        String[] testCases = {
                // 整數運算
                "5",
                "10",
                "5 + 5",
                "5 - 5",
                "5 * 5",
                "5 / 5",

                // 複雜表達式
                "2 * (5 + 10)",
                "(5 + 10 * 2 + 15 / 3) * 2 + -10",

                // 布林值
                "true",
                "false",
                "!true",
                "!false",

                // 比較運算
                "1 < 2",
                "1 > 2",
                "1 == 1",
                "1 != 2",
                "true == true",
                "false != true",

                // If 表達式
                "if (true) { 10 }",
                "if (false) { 10 } else { 20 }",
                "if (1 < 2) { 10 }",

                // Return 語句
                "return 10;",
                "return 2 * 5;",

                // Let 綁定
                "let a = 5; a;",
                "let a = 5 * 5; a;",
                "let a = 5; let b = a; let c = a + b + 5; c;",

                // 函數
                "let identity = fn(x) { x; }; identity(5);",
                "let add = fn(x, y) { x + y; }; add(5, 5);",
                "fn(x) { x; }(5)",

                // 閉包
                """
            let newAdder = fn(x) {
                fn(y) { x + y };
            };
            let addTwo = newAdder(2);
            addTwo(3);
            """,

                // 遞迴（階乘）
                """
            let factorial = fn(n) {
                if (n == 0) {
                    1
                } else {
                    n * factorial(n - 1)
                }
            };
            factorial(5);
            """,

                // 錯誤處理
                "5 + true",
                "-true",
                "foobar"
        };

        Environment env = new Environment();

        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("Test Case %d:%n", i + 1);
            System.out.println("-".repeat(60));
            System.out.println("Input:");
            System.out.println(input);
            System.out.println();

            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            if (!parser.getErrors().isEmpty()) {
                System.err.println("❌ Parser Errors:");
                for (String error : parser.getErrors()) {
                    System.err.println("  - " + error);
                }
                continue;
            }

            MonkeyObject evaluated = Evaluator.eval(program, env);
            if (evaluated != null) {
                System.out.println("Result:");
                System.out.println(evaluated.inspect());
                System.out.println("\nType: " + evaluated.type());
            } else {
                System.out.println("Result: (no value)");
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 Evaluator Demo Complete!");
        System.out.println("=".repeat(60));

        System.out.println("\nTry the REPL:");
        System.out.println("  java -jar monkey.jar");
        System.out.println("\nREPL Commands:");
        System.out.println("  exit or quit - Exit the REPL");
    }
}
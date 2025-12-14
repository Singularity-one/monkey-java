package com.monkey;

import com.monkey.ast.Program;
import com.monkey.lexer.Lexer;
import com.monkey.parser.Parser;
import com.monkey.token.Token;
import com.monkey.token.TokenType;

/**
 * Main 類用於測試 Lexer
 */
public class Main {
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

        System.out.println("=".repeat(60));
        System.out.println("Monkey Language Parser - Chapter 2 Demo");
        System.out.println("=".repeat(60));

        // 測試各種表達式和語句
        String[] testCases = {
                // Let 語句
                "let x = 5;",
                "let y = 10;",
                "let foobar = 838383;",

                // Return 語句
                "return 5;",
                "return x + y;",

                // 表達式語句
                "5;",
                "foobar;",

                // 前綴表達式
                "-5;",
                "!true;",
                "!false;",

                // 中綴表達式
                "5 + 5;",
                "5 - 5;",
                "5 * 5;",
                "5 / 5;",
                "5 > 5;",
                "5 < 5;",
                "5 == 5;",
                "5 != 5;",

                // 複雜表達式（測試優先級）
                "3 + 4 * 5;",
                "(5 + 5) * 2;",
                "2 / (5 + 5);",
                "-(5 + 5);",

                // 布林表達式
                "true == true;",
                "false != true;",
                "3 > 5 == false;",

                // If 表達式
                "if (x < y) { x }",
                "if (x < y) { x } else { y }",

                // 函數字面值
                "fn(x, y) { x + y; }",
                "fn() { return 5; }",

                // 函數調用
                "add(1, 2);",
                "add(1, 2 * 3, 4 + 5);",

                // 複雜程式
                """
            let add = fn(x, y) {
                x + y;
            };
            
            let result = add(5, 10);
            result;
            """
        };

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
            } else {
                System.out.println("✅ Parsed successfully!");
                System.out.println("\nAST:");
                System.out.println(program.string());

                System.out.println("\nNumber of statements: " + program.getStatements().size());
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 Parser Demo Complete!");
        System.out.println("=".repeat(60));

        // 展示一個完整的 Monkey 程式
        System.out.println("\nParsing a complete Monkey program:");
        System.out.println("=".repeat(60));

        String completeProgram = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
              x + y;
            };
            
            let result = add(five, ten);
            
            if (result > 10) {
                return true;
            } else {
                return false;
            }
            """;

        System.out.println(completeProgram);
        System.out.println("=".repeat(60));

        Lexer lexer = new Lexer(completeProgram);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        if (!parser.getErrors().isEmpty()) {
            System.err.println("❌ Parser Errors:");
            for (String error : parser.getErrors()) {
                System.err.println("  - " + error);
            }
        } else {
            System.out.println("✅ Complete program parsed successfully!");
            System.out.println("\nGenerated AST:");
            System.out.println(program.string());
        }
    }
}
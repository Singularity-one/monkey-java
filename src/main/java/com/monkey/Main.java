package com.monkey;

import com.monkey.lexer.Lexer;
import com.monkey.token.Token;
import com.monkey.token.TokenType;

/**
 * Main 類用於測試 Lexer
 */
public class Main {
    public static void main(String[] args) {
        String input = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
              x + y;
            };
            
            let result = add(five, ten);
            !-/*5;
            5 < 10 > 5;
            
            if (5 < 10) {
                return true;
            } else {
                return false;
            }
            
            10 == 10;
            10 != 9;
            """;

        System.out.println("Lexing the following input:");
        System.out.println("----------------------------");
        System.out.println(input);
        System.out.println("----------------------------");
        System.out.println("\nTokens generated:");
        System.out.println("----------------------------");

        Lexer lexer = new Lexer(input);

        Token tok = lexer.nextToken();
        int tokenCount = 0;

        while (tok.getType() != TokenType.EOF) {
            System.out.printf("%3d. Type: %-12s Literal: '%s'%n",
                    ++tokenCount,
                    tok.getType(),
                    tok.getLiteral());
            tok = lexer.nextToken();
        }

        System.out.println("----------------------------");
        System.out.println("Total tokens: " + tokenCount);
        System.out.println("\n✅ Lexer 運行完成！");
    }
}
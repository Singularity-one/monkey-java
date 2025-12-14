package com.monkey.lexer;

import com.monkey.token.Token;
import com.monkey.token.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lexer 測試類
 * 根據 "Writing An Interpreter In Go" 第一章的測試改寫
 */
public class LexerTest {

    @Test
    public void testNextToken_BasicOperators() {
        String input = "=+(){},;";

        Token[] expectedTokens = {
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (Token expectedToken : expectedTokens) {
            Token tok = lexer.nextToken();
            assertEquals(expectedToken.getType(), tok.getType(),
                    "Expected token type " + expectedToken.getType() + " but got " + tok.getType());
            assertEquals(expectedToken.getLiteral(), tok.getLiteral(),
                    "Expected literal '" + expectedToken.getLiteral() + "' but got '" + tok.getLiteral() + "'");
        }
    }

    @Test
    public void testNextToken_SimpleProgram() {
        String input = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
              x + y;
            };
            
            let result = add(five, ten);
            """;

        Token[] expectedTokens = {
                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "five"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),

                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "ten"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.SEMICOLON, ";"),

                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "add"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.FUNCTION, "fn"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.IDENT, "x"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENT, "y"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.IDENT, "x"),
                new Token(TokenType.PLUS, "+"),
                new Token(TokenType.IDENT, "y"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.SEMICOLON, ";"),

                new Token(TokenType.LET, "let"),
                new Token(TokenType.IDENT, "result"),
                new Token(TokenType.ASSIGN, "="),
                new Token(TokenType.IDENT, "add"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.IDENT, "five"),
                new Token(TokenType.COMMA, ","),
                new Token(TokenType.IDENT, "ten"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.SEMICOLON, ";"),

                new Token(TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (int i = 0; i < expectedTokens.length; i++) {
            Token tok = lexer.nextToken();
            assertEquals(expectedTokens[i].getType(), tok.getType(),
                    "Test [" + i + "] - Expected token type " + expectedTokens[i].getType() +
                            " but got " + tok.getType());
            assertEquals(expectedTokens[i].getLiteral(), tok.getLiteral(),
                    "Test [" + i + "] - Expected literal '" + expectedTokens[i].getLiteral() +
                            "' but got '" + tok.getLiteral() + "'");
        }
    }

    @Test
    public void testNextToken_ExtendedOperators() {
        String input = """
            !-/*5;
            5 < 10 > 5;
            """;

        Token[] expectedTokens = {
                new Token(TokenType.BANG, "!"),
                new Token(TokenType.MINUS, "-"),
                new Token(TokenType.SLASH, "/"),
                new Token(TokenType.ASTERISK, "*"),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.LT, "<"),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.GT, ">"),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (Token expectedToken : expectedTokens) {
            Token tok = lexer.nextToken();
            assertEquals(expectedToken.getType(), tok.getType());
            assertEquals(expectedToken.getLiteral(), tok.getLiteral());
        }
    }

    @Test
    public void testNextToken_Keywords() {
        String input = """
            if (5 < 10) {
                return true;
            } else {
                return false;
            }
            """;

        Token[] expectedTokens = {
                new Token(TokenType.IF, "if"),
                new Token(TokenType.LPAREN, "("),
                new Token(TokenType.INT, "5"),
                new Token(TokenType.LT, "<"),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.RPAREN, ")"),
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.TRUE, "true"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.ELSE, "else"),
                new Token(TokenType.LBRACE, "{"),
                new Token(TokenType.RETURN, "return"),
                new Token(TokenType.FALSE, "false"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.RBRACE, "}"),
                new Token(TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (Token expectedToken : expectedTokens) {
            Token tok = lexer.nextToken();
            assertEquals(expectedToken.getType(), tok.getType());
            assertEquals(expectedToken.getLiteral(), tok.getLiteral());
        }
    }

    @Test
    public void testNextToken_TwoCharacterOperators() {
        String input = """
            10 == 10;
            10 != 9;
            """;

        Token[] expectedTokens = {
                new Token(TokenType.INT, "10"),
                new Token(TokenType.EQ, "=="),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.INT, "10"),
                new Token(TokenType.NOT_EQ, "!="),
                new Token(TokenType.INT, "9"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (Token expectedToken : expectedTokens) {
            Token tok = lexer.nextToken();
            assertEquals(expectedToken.getType(), tok.getType());
            assertEquals(expectedToken.getLiteral(), tok.getLiteral());
        }
    }
}
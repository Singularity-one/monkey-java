package com.monkey.ast;
import com.monkey.token.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * BlockStatement 表示塊語句
 * { statement1; statement2; ... }
 */
public class BlockStatement implements Statement {
    private final Token token;                  // '{' token
    private final List<Statement> statements;   // 語句列表

    public BlockStatement(Token token) {
        this.token = token;
        this.statements = new ArrayList<>();
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }

    @Override
    public void statementNode() {
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        for (Statement stmt : statements) {
            sb.append(stmt.string());
        }
        return sb.toString();
    }
}
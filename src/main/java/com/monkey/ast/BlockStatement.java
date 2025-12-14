package com.monkey.ast;
import com.monkey.token.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * BlockStatement 代表區塊語句
 * 例如：{ x + y; }
 */
public class BlockStatement implements Statement {
    private final Token token; // { token
    private final List<Statement> statements;

    public BlockStatement(Token token) {
        this.token = token;
        this.statements = new ArrayList<>();
    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        StringBuilder out = new StringBuilder();
        for (Statement stmt : statements) {
            out.append(stmt.string());
        }
        return out.toString();
    }
}

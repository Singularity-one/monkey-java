package com.monkey.ast;
import com.monkey.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Program 是 AST 的根節點
 * 包含所有的語句
 */
public class Program implements Node {
    private final List<Statement> statements;

    public Program() {
        this.statements = new ArrayList<>();
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatement(Statement stmt) {
        statements.add(stmt);
    }

    @Override
    public String tokenLiteral() {
        if (!statements.isEmpty()) {
            return statements.get(0).tokenLiteral();
        }
        return "";
    }

    @Override
    public String string() {
        StringBuilder out = new StringBuilder();
        for (Statement stmt : statements) {
            out.append(stmt.string());
        }
        return out.toString();
    }

    @Override
    public Token getToken() {
        if (!statements.isEmpty()) {
            return statements.get(0).getToken();
        }
        return null; // 或 throw exception，看你設計
    }
}

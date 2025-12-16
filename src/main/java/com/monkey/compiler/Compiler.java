package com.monkey.compiler;

import com.monkey.ast.*;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.object.BooleanObject;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler 將 AST 編譯為字節碼
 * Chapter 3: Compiling Expressions (擴展)
 *
 * 新增功能:
 * - OpPop 指令 (清理堆疊)
 * - 布爾值編譯
 * - 比較運算編譯
 * - 前綴運算編譯
 */
public class Compiler {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
    }

    /**
     * 編譯 AST 節點
     */
    public void compile(Node node) throws CompilerException {
        if (node instanceof Program) {
            // 程序根節點
            Program program = (Program) node;
            for (Statement stmt : program.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof ExpressionStatement) {
            // 表達式語句
            ExpressionStatement exprStmt = (ExpressionStatement) node;
            compile(exprStmt.getExpression());
            // Chapter 3: 添加 OpPop 清理堆疊
            emit(Opcode.OP_POP);
        }
        else if (node instanceof InfixExpression) {
            // 中綴表達式
            InfixExpression infixExpr = (InfixExpression) node;

            // 特殊處理: < 轉換為 >
            // 因為我們只實現 OpGreaterThan
            // a < b 等價於 b > a
            if (infixExpr.getOperator().equals("<")) {
                // 交換操作數順序
                compile(infixExpr.getRight());
                compile(infixExpr.getLeft());
                emit(Opcode.OP_GREATER_THAN);
                return;
            }

            // 正常順序: 先左後右
            compile(infixExpr.getLeft());
            compile(infixExpr.getRight());

            // 發射運算符指令
            switch (infixExpr.getOperator()) {
                case "+":
                    emit(Opcode.OP_ADD);
                    break;
                case "-":
                    emit(Opcode.OP_SUB);
                    break;
                case "*":
                    emit(Opcode.OP_MUL);
                    break;
                case "/":
                    emit(Opcode.OP_DIV);
                    break;
                case ">":
                    emit(Opcode.OP_GREATER_THAN);
                    break;
                case "==":
                    emit(Opcode.OP_EQUAL);
                    break;
                case "!=":
                    emit(Opcode.OP_NOT_EQUAL);
                    break;
                default:
                    throw new CompilerException("unknown operator " + infixExpr.getOperator());
            }
        }
        else if (node instanceof PrefixExpression) {
            // 前綴表達式
            PrefixExpression prefixExpr = (PrefixExpression) node;

            // 先編譯操作數
            compile(prefixExpr.getRight());

            // 發射前綴運算符指令
            switch (prefixExpr.getOperator()) {
                case "!":
                    emit(Opcode.OP_BANG);
                    break;
                case "-":
                    emit(Opcode.OP_MINUS);
                    break;
                default:
                    throw new CompilerException("unknown operator " + prefixExpr.getOperator());
            }
        }
        else if (node instanceof IntegerLiteral) {
            // 整數字面量
            IntegerLiteral intLit = (IntegerLiteral) node;
            IntegerObject integer = new IntegerObject(intLit.getValue());
            emit(Opcode.OP_CONSTANT, addConstant(integer));
        }
        else if (node instanceof BooleanLiteral) {
            // 布爾字面量
            BooleanLiteral boolLit = (BooleanLiteral) node;
            if (boolLit.getValue()) {
                emit(Opcode.OP_TRUE);
            } else {
                emit(Opcode.OP_FALSE);
            }
        }
    }

    /**
     * 發射一條指令
     */
    private int emit(Opcode op, int... operands) {
        byte[] ins = Instructions.make(op, operands);
        int pos = addInstruction(ins);
        return pos;
    }

    /**
     * 添加指令到指令序列
     */
    private int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.size();
        instructions.append(ins);
        return posNewInstruction;
    }

    /**
     * 添加常量到常量池
     */
    private int addConstant(MonkeyObject obj) {
        constants.add(obj);
        return constants.size() - 1;
    }

    /**
     * 獲取編譯結果
     */
    public Bytecode bytecode() {
        return new Bytecode(instructions, constants);
    }

    /**
     * 編譯器異常
     */
    public static class CompilerException extends Exception {
        public CompilerException(String message) {
            super(message);
        }
    }
}

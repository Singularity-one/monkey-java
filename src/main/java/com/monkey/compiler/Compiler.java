package com.monkey.compiler;

import com.monkey.ast.*;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler 將 AST 編譯為字節碼
 *
 * 編譯過程:
 * 1. 遍歷 AST
 * 2. 為每個節點生成對應的字節碼指令
 * 3. 收集常量到常量池
 * 4. 輸出 Bytecode 對象
 *
 * Chapter 2: Hello Bytecode!
 */
public class Compiler {
    private final Instructions instructions;     // 累積的字節碼指令
    private final List<MonkeyObject> constants;  // 常量池

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
    }

    /**
     * 編譯 AST 節點
     *
     * 這是一個遞歸方法,會遍歷整個 AST 樹
     *
     * @param node AST 節點
     * @throws CompilerException 編譯錯誤
     */
    public void compile(Node node) throws CompilerException {
        if (node instanceof Program) {
            // 程序根節點 - 編譯所有語句
            Program program = (Program) node;
            for (Statement stmt : program.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof ExpressionStatement) {
            // 表達式語句 - 編譯表達式,然後彈出結果
            ExpressionStatement exprStmt = (ExpressionStatement) node;
            compile(exprStmt.getExpression());
            // Chapter 2: 暫時不彈出,讓結果留在堆疊上用於測試
            // Chapter 3 會添加 OpPop
        }
        else if (node instanceof InfixExpression) {
            // 中綴表達式 - 先編譯操作數,再發射運算符指令
            InfixExpression infixExpr = (InfixExpression) node;

            // 1. 編譯左操作數 (結果會被推入堆疊)
            compile(infixExpr.getLeft());

            // 2. 編譯右操作數 (結果也會被推入堆疊)
            compile(infixExpr.getRight());

            // 3. 根據運算符發射對應的指令
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
                default:
                    throw new CompilerException("unknown operator " + infixExpr.getOperator());
            }
        }
        else if (node instanceof IntegerLiteral) {
            // 整數字面量 - 添加到常量池並發射 OpConstant 指令
            IntegerLiteral intLit = (IntegerLiteral) node;

            // 1. 將整數轉換為對象
            IntegerObject integer = new IntegerObject(intLit.getValue());

            // 2. 添加到常量池,獲取索引
            int constIndex = addConstant(integer);

            // 3. 發射 OpConstant 指令,操作數是常量池索引
            emit(Opcode.OP_CONSTANT, constIndex);
        }
    }

    /**
     * 發射一條指令
     *
     * "發射"(emit) 是編譯器術語,表示生成並輸出一條指令
     *
     * @param op 操作碼
     * @param operands 操作數
     * @return 指令的起始位置 (用於回填)
     */
    private int emit(Opcode op, int... operands) {
        // 1. 創建指令
        byte[] ins = Instructions.make(op, operands);

        // 2. 添加到指令序列
        int pos = addInstruction(ins);

        // 3. 返回位置 (Chapter 4 條件語句會用到)
        return pos;
    }

    /**
     * 添加指令到指令序列
     *
     * @param ins 指令字節數組
     * @return 新指令的起始位置
     */
    private int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.size();
        instructions.append(ins);
        return posNewInstruction;
    }

    /**
     * 添加常量到常量池
     *
     * 常量池用於存儲所有編譯時的常量:
     * - 整數: 1, 2, 100, -50 等
     * - 布爾值: true, false (Chapter 3)
     * - 字符串: "hello" (Chapter 6)
     * - 函數: fn(x) { x + 1 } (Chapter 7)
     *
     * @param obj 常量對象
     * @return 常量在常量池中的索引
     */
    private int addConstant(MonkeyObject obj) {
        constants.add(obj);
        return constants.size() - 1;
    }

    /**
     * 獲取編譯結果
     *
     * @return 字節碼對象,包含指令和常量池
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

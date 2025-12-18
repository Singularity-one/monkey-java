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
 * Chapter 4: Conditionals
 */
public class Compiler {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    // Chapter 4: 追蹤最後發射的指令
    private EmittedInstruction lastInstruction;
    private EmittedInstruction previousInstruction;

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
        this.lastInstruction = null;
        this.previousInstruction = null;
    }

    /**
     * 編譯 AST 節點
     */
    public void compile(Node node) throws CompilerException {
        if (node instanceof Program) {
            Program program = (Program) node;
            for (Statement stmt : program.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof ExpressionStatement) {
            ExpressionStatement exprStmt = (ExpressionStatement) node;
            compile(exprStmt.getExpression());
            emit(Opcode.OP_POP);
        }
        else if (node instanceof BlockStatement) {
            // Chapter 4: 區塊語句
            BlockStatement block = (BlockStatement) node;
            for (Statement stmt : block.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof IfExpression) {
            // Chapter 4: if 表達式
            compileIfExpression((IfExpression) node);
        }
        else if (node instanceof InfixExpression) {
            InfixExpression infixExpr = (InfixExpression) node;

            // 特殊處理: < 轉換為 >
            if (infixExpr.getOperator().equals("<")) {
                compile(infixExpr.getRight());
                compile(infixExpr.getLeft());
                emit(Opcode.OP_GREATER_THAN);
                return;
            }

            compile(infixExpr.getLeft());
            compile(infixExpr.getRight());

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
            PrefixExpression prefixExpr = (PrefixExpression) node;
            compile(prefixExpr.getRight());

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
            IntegerLiteral intLit = (IntegerLiteral) node;
            IntegerObject integer = new IntegerObject(intLit.getValue());
            emit(Opcode.OP_CONSTANT, addConstant(integer));
        }
        else if (node instanceof BooleanLiteral) {
            BooleanLiteral boolLit = (BooleanLiteral) node;
            if (boolLit.getValue()) {
                emit(Opcode.OP_TRUE);
            } else {
                emit(Opcode.OP_FALSE);
            }
        }
    }

    /**
     * Chapter 4: 編譯 if 表達式
     *
     * 編譯模式:
     * 有 else:
     *   <condition>
     *   OpJumpNotTruthy <afterConsequence>
     *   <consequence>
     *   OpJump <afterAlternative>
     *   <alternative>
     *
     * 無 else:
     *   <condition>
     *   OpJumpNotTruthy <afterConsequence>
     *   <consequence>
     *   OpJump <afterNull>
     *   OpNull
     */
    private void compileIfExpression(IfExpression ifExpr) throws CompilerException {
        // 1. 編譯條件
        compile(ifExpr.getCondition());

        // 2. 發射條件跳轉 (先用假地址 9999)
        int jumpNotTruthyPos = emit(Opcode.OP_JUMP_NOT_TRUTHY, 9999);

        // 3. 編譯 consequence
        compile(ifExpr.getConsequence());

        // 移除 consequence 末尾的 OpPop
        if (lastInstructionIs(Opcode.OP_POP)) {
            removeLastPop();
        }

        // 4. 發射無條件跳轉 (跳過 alternative)
        int jumpPos = emit(Opcode.OP_JUMP, 9999);

        // 5. 回填條件跳轉的目標地址
        int afterConsequencePos = instructions.size();
        changeOperand(jumpNotTruthyPos, afterConsequencePos);

        // 6. 編譯 alternative 或推入 null
        if (ifExpr.getAlternative() == null) {
            emit(Opcode.OP_NULL);
        } else {
            compile(ifExpr.getAlternative());
            if (lastInstructionIs(Opcode.OP_POP)) {
                removeLastPop();
            }
        }

        // 7. 回填無條件跳轉的目標地址
        int afterAlternativePos = instructions.size();
        changeOperand(jumpPos, afterAlternativePos);
    }

    /**
     * 發射一條指令
     */
    private int emit(Opcode op, int... operands) {
        byte[] ins = Instructions.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op, pos);
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
     * Chapter 4: 設置最後發射的指令
     */
    private void setLastInstruction(Opcode op, int pos) {
        previousInstruction = lastInstruction;
        lastInstruction = new EmittedInstruction(op, pos);
    }

    /**
     * Chapter 4: 檢查最後一條指令是否是指定的操作碼
     */
    private boolean lastInstructionIs(Opcode op) {
        if (instructions.size() == 0) {
            return false;
        }
        return lastInstruction != null && lastInstruction.opcode == op;
    }

    /**
     * Chapter 4: 移除最後一條 OpPop 指令
     *
     * 這裡我們需要實際從指令序列中移除最後一條指令
     * 由於 Instructions 使用 ArrayList<Byte>,我們需要移除最後一個字節
     */
    private void removeLastPop() {
        if (lastInstruction == null || lastInstruction.opcode != Opcode.OP_POP) {
            return;
        }

        // OpPop 只有 1 字節,直接移除
        instructions.removeLast();

        // 恢復到前一條指令
        lastInstruction = previousInstruction;
    }

    /**
     * Chapter 4: 修改指定位置的操作數
     */
    private void changeOperand(int opPos, int operand) {
        instructions.changeOperand(opPos, operand);
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
     * Chapter 4: 記錄發射的指令
     */
    private static class EmittedInstruction {
        Opcode opcode;
        int position;

        EmittedInstruction(Opcode opcode, int position) {
            this.opcode = opcode;
            this.position = position;
        }
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
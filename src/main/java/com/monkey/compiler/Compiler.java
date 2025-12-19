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
 * Chapter 5: Keeping Track of Names (擴展)
 *
 * 新增功能:
 * - let 語句編譯
 * - 標識符編譯
 * - 符號表管理
 */
public class Compiler {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    private EmittedInstruction lastInstruction;
    private EmittedInstruction previousInstruction;

    // Chapter 5: 符號表
    private final SymbolTable symbolTable;

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
        this.symbolTable = new SymbolTable();
    }

    /**
     * 帶符號表的構造函數 (用於測試)
     */
    public Compiler(SymbolTable symbolTable) {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
        this.symbolTable = symbolTable;
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
            BlockStatement block = (BlockStatement) node;
            for (Statement stmt : block.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof LetStatement) {
            // Chapter 5: 編譯 let 語句
            compileLetStatement((LetStatement) node);
        }
        else if (node instanceof IfExpression) {
            compileIfExpression((IfExpression) node);
        }
        else if (node instanceof InfixExpression) {
            InfixExpression infixExpr = (InfixExpression) node;

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
        else if (node instanceof Identifier) {
            // Chapter 5: 編譯標識符
            compileIdentifier((Identifier) node);
        }
    }

    /**
     * Chapter 5: 編譯 let 語句
     *
     * let x = 10;
     *
     * 編譯為:
     *   <compile value>
     *   OpSetGlobal <index>
     */
    private void compileLetStatement(LetStatement letStmt) throws CompilerException {
        // 1. 編譯值表達式
        compile(letStmt.getValue());

        // 2. 在符號表中定義變量
        Symbol symbol = symbolTable.define(letStmt.getName().getValue());

        // 3. 發射 SetGlobal 指令
        emit(Opcode.OP_SET_GLOBAL, symbol.getIndex());
    }

    /**
     * Chapter 5: 編譯標識符
     *
     * x
     *
     * 編譯為:
     *   OpGetGlobal <index>
     */
    private void compileIdentifier(Identifier ident) throws CompilerException {
        // 1. 在符號表中查找變量
        Symbol symbol = symbolTable.resolve(ident.getValue());

        if (symbol == null) {
            throw new CompilerException("undefined variable " + ident.getValue());
        }

        // 2. 發射 GetGlobal 指令
        emit(Opcode.OP_GET_GLOBAL, symbol.getIndex());
    }

    /**
     * 編譯 if 表達式
     */
    private void compileIfExpression(IfExpression ifExpr) throws CompilerException {
        compile(ifExpr.getCondition());

        int jumpNotTruthyPos = emit(Opcode.OP_JUMP_NOT_TRUTHY, 9999);

        compile(ifExpr.getConsequence());

        if (lastInstructionIs(Opcode.OP_POP)) {
            removeLastPop();
        }

        int jumpPos = emit(Opcode.OP_JUMP, 9999);

        int afterConsequencePos = instructions.size();
        changeOperand(jumpNotTruthyPos, afterConsequencePos);

        if (ifExpr.getAlternative() == null) {
            emit(Opcode.OP_NULL);
        } else {
            compile(ifExpr.getAlternative());

            if (lastInstructionIs(Opcode.OP_POP)) {
                removeLastPop();
            }
        }

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
     * 設置最後發射的指令
     */
    private void setLastInstruction(Opcode op, int pos) {
        previousInstruction = lastInstruction;
        lastInstruction = new EmittedInstruction(op, pos);
    }

    /**
     * 檢查最後一條指令是否是指定的操作碼
     */
    private boolean lastInstructionIs(Opcode op) {
        if (instructions.size() == 0) {
            return false;
        }
        return lastInstruction != null && lastInstruction.getOpcode() == op;
    }

    /**
     * 移除最後一條 OpPop 指令
     */
    private void removeLastPop() {
        if (lastInstruction != null && lastInstruction.getOpcode() == Opcode.OP_POP) {
            instructions.removeLast(1);
            lastInstruction = previousInstruction;
        }
    }

    /**
     * 修改指定位置的操作數
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
     * 獲取符號表 (用於測試)
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
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
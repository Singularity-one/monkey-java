package com.monkey.compiler;

import com.monkey.ast.*;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Compiler 將 AST 編譯為字節碼
 * Chapter 6: String, Array and Hash (擴展)
 *
 * 新增功能:
 * - 字串字面量編譯
 * - 陣列字面量編譯
 * - 雜湊表字面量編譯
 * - 索引表達式編譯
 */
public class Compiler {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    private EmittedInstruction lastInstruction;
    private EmittedInstruction previousInstruction;

    private final SymbolTable symbolTable;

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
        this.symbolTable = new SymbolTable();
    }

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
        // Chapter 6: 字串字面量
        else if (node instanceof StringLiteral) {
            StringLiteral strLit = (StringLiteral) node;
            StringObject str = new StringObject(strLit.getValue());
            emit(Opcode.OP_CONSTANT, addConstant(str));
        }
        else if (node instanceof BooleanLiteral) {
            BooleanLiteral boolLit = (BooleanLiteral) node;
            if (boolLit.getValue()) {
                emit(Opcode.OP_TRUE);
            } else {
                emit(Opcode.OP_FALSE);
            }
        }
        // Chapter 6: 陣列字面量
        else if (node instanceof ArrayLiteral) {
            compileArrayLiteral((ArrayLiteral) node);
        }
        // Chapter 6: 雜湊表字面量
        else if (node instanceof HashLiteral) {
            compileHashLiteral((HashLiteral) node);
        }
        // Chapter 6: 索引表達式
        else if (node instanceof IndexExpression) {
            compileIndexExpression((IndexExpression) node);
        }
        else if (node instanceof Identifier) {
            compileIdentifier((Identifier) node);
        }
    }

    /**
     * Chapter 6: 編譯陣列字面量
     *
     * [1, 2, 3]
     *
     * 編譯為:
     *   OpConstant 0  // 1
     *   OpConstant 1  // 2
     *   OpConstant 2  // 3
     *   OpArray 3     // 構建包含 3 個元素的陣列
     */
    private void compileArrayLiteral(ArrayLiteral array) throws CompilerException {
        // 1. 編譯每個元素
        for (Expression element : array.getElements()) {
            compile(element);
        }

        // 2. 發射 OpArray 指令，操作數是元素數量
        emit(Opcode.OP_ARRAY, array.getElements().size());
    }

    /**
     * Chapter 6: 編譯雜湊表字面量
     *
     * {1: 2, 3: 4}
     *
     * 編譯為:
     *   OpConstant 0  // 1 (key)
     *   OpConstant 1  // 2 (value)
     *   OpConstant 2  // 3 (key)
     *   OpConstant 3  // 4 (value)
     *   OpHash 4      // 構建包含 4 個元素 (2 對鍵值對) 的雜湊表
     */
    private void compileHashLiteral(HashLiteral hash) throws CompilerException {
        // 1. 排序 keys 以保證順序一致性
        List<Expression> keys = new ArrayList<>(hash.getPairs().keySet());
        keys.sort(Comparator.comparing(Node::string));

        // 2. 按順序編譯每對鍵值
        for (Expression key : keys) {
            compile(key);
            compile(hash.getPairs().get(key));
        }

        // 3. 發射 OpHash 指令，操作數是鍵值對總數 (keys + values)
        emit(Opcode.OP_HASH, hash.getPairs().size() * 2);
    }

    /**
     * Chapter 6: 編譯索引表達式
     *
     * array[index]
     *
     * 編譯為:
     *   <compile array>
     *   <compile index>
     *   OpIndex
     */
    private void compileIndexExpression(IndexExpression indexExpr) throws CompilerException {
        // 1. 編譯被索引的對象
        compile(indexExpr.getLeft());

        // 2. 編譯索引
        compile(indexExpr.getIndex());

        // 3. 發射 OpIndex 指令
        emit(Opcode.OP_INDEX);
    }

    /**
     * Chapter 5: 編譯 let 語句
     */
    private void compileLetStatement(LetStatement letStmt) throws CompilerException {
        compile(letStmt.getValue());
        Symbol symbol = symbolTable.define(letStmt.getName().getValue());
        emit(Opcode.OP_SET_GLOBAL, symbol.getIndex());
    }

    /**
     * Chapter 5: 編譯標識符
     */
    private void compileIdentifier(Identifier ident) throws CompilerException {
        Symbol symbol = symbolTable.resolve(ident.getValue());
        if (symbol == null) {
            throw new CompilerException("undefined variable " + ident.getValue());
        }
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

    private int emit(Opcode op, int... operands) {
        byte[] ins = Instructions.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op, pos);
        return pos;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.size();
        instructions.append(ins);
        return posNewInstruction;
    }

    private void setLastInstruction(Opcode op, int pos) {
        previousInstruction = lastInstruction;
        lastInstruction = new EmittedInstruction(op, pos);
    }

    private boolean lastInstructionIs(Opcode op) {
        if (instructions.size() == 0) {
            return false;
        }
        return lastInstruction != null && lastInstruction.getOpcode() == op;
    }

    private void removeLastPop() {
        if (lastInstruction != null && lastInstruction.getOpcode() == Opcode.OP_POP) {
            instructions.removeLast(1);
            lastInstruction = previousInstruction;
        }
    }

    private void changeOperand(int opPos, int operand) {
        instructions.changeOperand(opPos, operand);
    }

    private int addConstant(MonkeyObject obj) {
        constants.add(obj);
        return constants.size() - 1;
    }

    public Bytecode bytecode() {
        return new Bytecode(instructions, constants);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static class CompilerException extends Exception {
        public CompilerException(String message) {
            super(message);
        }
    }
}
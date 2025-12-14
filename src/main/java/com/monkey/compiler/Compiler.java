package com.monkey.compiler;

import com.monkey.ast.*;
import com.monkey.code.*;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler 將 AST 編譯為字節碼
 */
public class Compiler {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    private EmittedInstruction lastInstruction;
    private EmittedInstruction previousInstruction;

    public Compiler() {
        this.instructions = new Instructions();
        this.constants = new ArrayList<>();
    }

    /**
     * 編譯節點
     */
    public void compile(Node node) throws CompilerException {
        if (node instanceof Program program) {
            for (Statement stmt : program.getStatements()) {
                compile(stmt);
            }
        } else if (node instanceof ExpressionStatement stmt) {
            compile(stmt.getExpression());
            emit(Opcode.OpPop);
        } else if (node instanceof InfixExpression infix) {
            if (infix.getOperator().equals("<")) {
                compile(infix.getRight());
                compile(infix.getLeft());
                emit(Opcode.OpGreaterThan);
                return;
            }

            compile(infix.getLeft());
            compile(infix.getRight());

            switch (infix.getOperator()) {
                case "+" -> emit(Opcode.OpAdd);
                case "-" -> emit(Opcode.OpSub);
                case "*" -> emit(Opcode.OpMul);
                case "/" -> emit(Opcode.OpDiv);
                case ">" -> emit(Opcode.OpGreaterThan);
                case "==" -> emit(Opcode.OpEqual);
                case "!=" -> emit(Opcode.OpNotEqual);
                default -> throw new CompilerException("unknown operator " + infix.getOperator());
            }
        } else if (node instanceof PrefixExpression prefix) {
            compile(prefix.getRight());

            switch (prefix.getOperator()) {
                case "!" -> emit(Opcode.OpBang);
                case "-" -> emit(Opcode.OpMinus);
                default -> throw new CompilerException("unknown operator " + prefix.getOperator());
            }
        } else if (node instanceof IntegerLiteral intLit) {
            IntegerObject integer = new IntegerObject(intLit.getValue());
            int constIndex = addConstant(integer);
            emit(Opcode.OpConstant, constIndex);
        } else if (node instanceof BooleanLiteral boolLit) {
            emit(boolLit.getValue() ? Opcode.OpTrue : Opcode.OpFalse);
        } else if (node instanceof IfExpression ifExpr) {
            compile(ifExpr.getCondition());

            // 條件跳轉地址先填 0
            int jumpNotTruthyPos = emit(Opcode.OpJumpNotTruthy, 0);

            // 編譯 consequence
            compile(ifExpr.getConsequence());
            if (lastInstructionIs(Opcode.OpPop)) {
                removeLastPop();
            }

            // 無條件跳轉，跳過 alternative
            int jumpPos = emit(Opcode.OpJump, 0);

            int afterConsequencePos = instructions.size();
            changeOperand(jumpNotTruthyPos, afterConsequencePos);

            if (ifExpr.getAlternative() != null) {
                compile(ifExpr.getAlternative());
                if (lastInstructionIs(Opcode.OpPop)) {
                    removeLastPop();
                }
            } else {
                emit(Opcode.OpNull); // 保證堆疊有值
            }

            int afterAlternativePos = instructions.size();
            changeOperand(jumpPos, afterAlternativePos);
        } else if (node instanceof BlockStatement block) {
            for (Statement stmt : block.getStatements()) {
                compile(stmt);
            }
        }
    }

    /**
     * 發出指令
     */
    private int emit(Opcode op, int... operands) {
        byte[] ins = Code.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op, pos);
        return pos;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = instructions.size();
        instructions.addAll(ins);
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
        if (lastInstruction != null && lastInstruction.getOpcode() == Opcode.OpPop) {
            int newSize = lastInstruction.getPosition();
            Instructions newInstructions = new Instructions();
            for (int i = 0; i < newSize; i++) {
                newInstructions.add(instructions.get(i));
            }
            instructions.clear();
            instructions.addAll(newInstructions);
            lastInstruction = previousInstruction;
        }
    }

    private void changeOperand(int pos, int operand) {
        Opcode op = Opcode.lookup(instructions.get(pos));
        byte[] newInstruction = Code.make(op, operand);
        for (int i = 0; i < newInstruction.length; i++) {
            instructions.set(pos + i, newInstruction[i]);
        }
    }

    private int addConstant(MonkeyObject obj) {
        constants.add(obj);
        return constants.size() - 1;
    }

    public BytecodeResult bytecode() {
        return new BytecodeResult(instructions, constants);
    }

    public static class CompilerException extends Exception {
        public CompilerException(String message) {
            super(message);
        }
    }
}

package com.monkey.vm;

import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.compiler.Bytecode;
import com.monkey.object.BooleanObject;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.object.NullObject;

import java.util.List;

/**
 * VM 是棧式虛擬機
 * Chapter 3: Compiling Expressions
 */
public class VM {
    private static final int STACK_SIZE = 2048;

    private final List<MonkeyObject> constants;
    private final Instructions instructions;
    private final MonkeyObject[] stack;
    private int sp;

    public VM(Bytecode bytecode) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants();
        this.stack = new MonkeyObject[STACK_SIZE];
        this.sp = 0;
    }

    /**
     * 獲取堆疊頂部元素
     */
    public MonkeyObject stackTop() {
        if (sp == 0) {
            return null;
        }
        return stack[sp - 1];
    }

    /**
     * 獲取最後彈出的元素
     * Chapter 3: 用於測試表達式語句的結果
     */
    public MonkeyObject lastPoppedStackElem() {
        return stack[sp];
    }

    /**
     * 運行虛擬機
     */
    public void run() throws VMException {
        for (int ip = 0; ip < instructions.size(); ip++) {
            byte opByte = instructions.get(ip);
            Opcode op = Opcode.fromByte(opByte);

            switch (op) {
                case OP_CONSTANT:
                    // 載入常量
                    Instructions.Definition def = Instructions.lookup(opByte);
                    byte[] insSlice = new byte[instructions.size() - (ip + 1)];
                    for (int j = 0; j < insSlice.length; j++) {
                        insSlice[j] = instructions.get(ip + 1 + j);
                    }
                    Instructions.ReadOperandsResult operandsRead = Instructions.readOperands(def, insSlice);
                    int constIndex = operandsRead.operands[0];
                    ip += operandsRead.bytesRead;

                    MonkeyObject constant = constants.get(constIndex);
                    if (constant == null) {
                        throw new VMException("constant at index " + constIndex + " is null");
                    }
                    push(constant);
                    break;

                case OP_ADD:
                case OP_SUB:
                case OP_MUL:
                case OP_DIV:
                    executeBinaryOperation(op);
                    break;

                case OP_TRUE:
                    push(BooleanObject.TRUE);
                    break;

                case OP_FALSE:
                    push(BooleanObject.FALSE);
                    break;

                case OP_EQUAL:
                case OP_NOT_EQUAL:
                case OP_GREATER_THAN:
                    executeComparison(op);
                    break;

                case OP_BANG:
                    executeBangOperator();
                    break;

                case OP_MINUS:
                    executeMinusOperator();
                    break;

                case OP_POP:
                    pop();
                    break;
            }
        }
    }

    /**
     * 執行二元運算
     */
    private void executeBinaryOperation(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            executeBinaryIntegerOperation(op, (IntegerObject) left, (IntegerObject) right);
        } else {
            throw new VMException(String.format("unsupported types for binary operation: %s %s",
                    left.type(), right.type()));
        }
    }

    /**
     * 執行整數二元運算
     */
    private void executeBinaryIntegerOperation(Opcode op, IntegerObject left, IntegerObject right)
            throws VMException {
        long leftValue = left.getValue();
        long rightValue = right.getValue();
        long result;

        switch (op) {
            case OP_ADD:
                result = leftValue + rightValue;
                break;
            case OP_SUB:
                result = leftValue - rightValue;
                break;
            case OP_MUL:
                result = leftValue * rightValue;
                break;
            case OP_DIV:
                if (rightValue == 0) {
                    throw new VMException("division by zero");
                }
                result = leftValue / rightValue;
                break;
            default:
                throw new VMException("unknown integer operator: " + op);
        }

        push(new IntegerObject(result));
    }

    /**
     * 執行比較運算
     */
    private void executeComparison(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            executeIntegerComparison(op, (IntegerObject) left, (IntegerObject) right);
        } else if (left instanceof BooleanObject && right instanceof BooleanObject) {
            executeBooleanComparison(op, (BooleanObject) left, (BooleanObject) right);
        } else {
            throw new VMException(String.format("unsupported types for comparison: %s %s",
                    left.type(), right.type()));
        }
    }

    /**
     * 執行整數比較
     */
    private void executeIntegerComparison(Opcode op, IntegerObject left, IntegerObject right)
            throws VMException {
        long leftValue = left.getValue();
        long rightValue = right.getValue();
        boolean result;

        switch (op) {
            case OP_EQUAL:
                result = leftValue == rightValue;
                break;
            case OP_NOT_EQUAL:
                result = leftValue != rightValue;
                break;
            case OP_GREATER_THAN:
                result = leftValue > rightValue;
                break;
            default:
                throw new VMException("unknown integer comparison operator: " + op);
        }

        push(BooleanObject.valueOf(result));
    }

    /**
     * 執行布林比較
     */
    private void executeBooleanComparison(Opcode op, BooleanObject left, BooleanObject right)
            throws VMException {
        boolean leftValue = left.getValue();
        boolean rightValue = right.getValue();
        boolean result;

        switch (op) {
            case OP_EQUAL:
                result = leftValue == rightValue;
                break;
            case OP_NOT_EQUAL:
                result = leftValue != rightValue;
                break;
            default:
                throw new VMException("unknown boolean comparison operator: " + op);
        }

        push(BooleanObject.valueOf(result));
    }

    /**
     * 執行邏輯非運算 !
     */
    private void executeBangOperator() throws VMException {
        MonkeyObject operand = pop();

        if (operand == BooleanObject.TRUE) {
            push(BooleanObject.FALSE);
        } else if (operand == BooleanObject.FALSE) {
            push(BooleanObject.TRUE);
        } else if (operand == NullObject.NULL) {
            push(BooleanObject.TRUE);
        } else {
            push(BooleanObject.FALSE);
        }
    }

    /**
     * 執行一元減號 -
     */
    private void executeMinusOperator() throws VMException {
        MonkeyObject operand = pop();

        if (!(operand instanceof IntegerObject)) {
            throw new VMException("unsupported type for negation: " + operand.type());
        }

        long value = ((IntegerObject) operand).getValue();
        push(new IntegerObject(-value));
    }

    /**
     * 將對象推入堆疊
     */
    private void push(MonkeyObject obj) throws VMException {
        if (sp >= STACK_SIZE) {
            throw new VMException("stack overflow");
        }
        stack[sp] = obj;
        sp++;
    }

    /**
     * 從堆疊彈出對象
     */
    private MonkeyObject pop() {
        MonkeyObject o = stack[sp - 1];
        sp--;
        return o;
    }

    /**
     * 虛擬機異常
     */
    public static class VMException extends Exception {
        public VMException(String message) {
            super(message);
        }
    }
}
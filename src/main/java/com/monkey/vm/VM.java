package com.monkey.vm;
import com.monkey.code.*;
import com.monkey.compiler.BytecodeResult;
import com.monkey.object.*;

/**
 * VM 是堆疊式虛擬機
 */
public class VM {
    private static final int STACK_SIZE = 2048;
    private static final NullObject NULL = NullObject.NULL;
    private static final BooleanObject TRUE = BooleanObject.TRUE;
    private static final BooleanObject FALSE = BooleanObject.FALSE;

    private final Instructions instructions;
    private final MonkeyObject[] constants;

    private final MonkeyObject[] stack;
    private int sp; // 堆疊指針，指向下一個空位

    public VM(BytecodeResult bytecode) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants().toArray(new MonkeyObject[0]);
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
     * 執行字節碼
     */
    public void run() throws VMException {
        int ip = 0; // 指令指針

        while (ip < instructions.size()) {
            // 獲取當前指令
            byte opByte = instructions.get(ip);
            Opcode op = Opcode.lookup(opByte);

            if (op == null) {
                throw new VMException("unknown opcode: " + opByte);
            }

            switch (op) {
                case OpConstant -> {
                    int constIndex = Code.readUint16(instructions.toByteArray(), ip + 1);
                    ip += 2;
                    push(constants[constIndex]);
                }

                case OpAdd, OpSub, OpMul, OpDiv -> {
                    executeBinaryOperation(op);
                }

                case OpTrue -> push(TRUE);

                case OpFalse -> push(FALSE);

                case OpEqual, OpNotEqual, OpGreaterThan -> {
                    executeComparison(op);
                }

                case OpBang -> {
                    executeBangOperator();
                }

                case OpMinus -> {
                    executeMinusOperator();
                }

                case OpJump -> {
                    int pos = Code.readUint16(instructions.toByteArray(), ip + 1);
                    ip = pos - 1; // -1 因為循環結尾會 +1
                }

                case OpJumpNotTruthy -> {
                    int pos = Code.readUint16(instructions.toByteArray(), ip + 1);
                    ip += 2;

                    MonkeyObject condition = pop();
                    if (!isTruthy(condition)) {
                        ip = pos - 1; // -1 因為循環結尾會 +1
                    }
                }

                case OpNull -> push(NULL);

                case OpPop -> pop();
            }

            ip++;
        }
    }

    /**
     * 執行二元運算
     */
    private void executeBinaryOperation(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            executeBinaryIntegerOperation(op,
                    (IntegerObject) left,
                    (IntegerObject) right);
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

        long result = switch (op) {
            case OpAdd -> leftValue + rightValue;
            case OpSub -> leftValue - rightValue;
            case OpMul -> leftValue * rightValue;
            case OpDiv -> {
                if (rightValue == 0) {
                    throw new VMException("division by zero");
                }
                yield leftValue / rightValue;
            }
            default -> throw new VMException("unknown integer operator: " + op);
        };

        push(new IntegerObject(result));
    }

    /**
     * 執行比較運算
     */
    private void executeComparison(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            executeIntegerComparison(op,
                    (IntegerObject) left,
                    (IntegerObject) right);
        } else {
            switch (op) {
                case OpEqual -> push(nativeBoolToBooleanObject(left == right));
                case OpNotEqual -> push(nativeBoolToBooleanObject(left != right));
                default -> throw new VMException(String.format(
                        "unknown operator: %s (%s %s)", op, left.type(), right.type()));
            }
        }
    }

    /**
     * 執行整數比較
     */
    private void executeIntegerComparison(Opcode op, IntegerObject left, IntegerObject right)
            throws VMException {
        long leftValue = left.getValue();
        long rightValue = right.getValue();

        boolean result = switch (op) {
            case OpEqual -> leftValue == rightValue;
            case OpNotEqual -> leftValue != rightValue;
            case OpGreaterThan -> leftValue > rightValue;
            default -> throw new VMException("unknown operator: " + op);
        };

        push(nativeBoolToBooleanObject(result));
    }

    /**
     * 執行 ! 運算符
     */
    private void executeBangOperator() throws VMException {
        MonkeyObject operand = pop();

        if (operand == TRUE) {
            push(FALSE);
        } else if (operand == FALSE) {
            push(TRUE);
        } else if (operand == NULL) {
            push(TRUE);
        } else {
            push(FALSE);
        }
    }

    /**
     * 執行 - 運算符
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
     * 判斷值是否為真
     */
    private boolean isTruthy(MonkeyObject obj) {
        if (obj == TRUE) {
            return true;
        } else if (obj == FALSE) {
            return false;
        } else if (obj == NULL) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 將 Java boolean 轉換為 BooleanObject
     */
    private BooleanObject nativeBoolToBooleanObject(boolean input) {
        return input ? TRUE : FALSE;
    }

    /**
     * 壓入堆疊
     */
    private void push(MonkeyObject obj) throws VMException {
        if (sp >= STACK_SIZE) {
            throw new VMException("stack overflow");
        }

        stack[sp] = obj;
        sp++;
    }

    /**
     * 彈出堆疊
     */
    private MonkeyObject pop() throws VMException {
        if (sp == 0) {
            throw new VMException("stack underflow");
        }

        MonkeyObject obj = stack[sp - 1];
        sp--;
        return obj;
    }

    /**
     * 獲取最後彈出的元素（結果）
     */
    public MonkeyObject lastPoppedStackElem() {
        return stack[sp];
    }

    /**
     * VM 異常
     */
    public static class VMException extends Exception {
        public VMException(String message) {
            super(message);
        }
    }
}

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
 * Chapter 5: Keeping Track of Names (擴展)
 *
 * 新增功能:
 * - 全局變量存儲
 * - OpSetGlobal, OpGetGlobal 指令執行
 */
public class VM {
    private static final int STACK_SIZE = 2048;
    private static final int GLOBALS_SIZE = 65536;  // Chapter 5: 最多 65536 個全局變量

    // 單例對象
    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);
    public static final NullObject NULL = new NullObject();

    private final List<MonkeyObject> constants;
    private final Instructions instructions;
    private final MonkeyObject[] stack;
    private int sp;

    // Chapter 5: 全局變量存儲
    private final MonkeyObject[] globals;

    public VM(Bytecode bytecode) {
        this(bytecode, new MonkeyObject[GLOBALS_SIZE]);
    }

    /**
     * Chapter 5: 帶全局變量的構造函數
     * 用於在多次編譯之間保持全局變量狀態
     */
    public VM(Bytecode bytecode, MonkeyObject[] globals) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants();
        this.stack = new MonkeyObject[STACK_SIZE];
        this.sp = 0;
        this.globals = globals;
    }

    public MonkeyObject stackTop() {
        if (sp == 0) {
            return null;
        }
        return stack[sp - 1];
    }

    public MonkeyObject lastPoppedStackElem() {
        return stack[sp];
    }

    /**
     * Chapter 5: 獲取全局變量數組
     * 用於在多次編譯之間保持狀態
     */
    public MonkeyObject[] getGlobals() {
        return globals;
    }

    /**
     * 運行虛擬機
     */
    public void run() throws VMException {
        for (int ip = 0; ip < instructions.size(); ip++) {
            // 取指
            byte opByte = instructions.get(ip);
            Opcode op = Opcode.fromByte(opByte);

            // 解碼和執行
            switch (op) {
                case OP_CONSTANT:
                    // 載入常量
                    byte[] constIndexBytes = new byte[2];
                    constIndexBytes[0] = instructions.get(ip + 1);
                    constIndexBytes[1] = instructions.get(ip + 2);
                    int constIndex = Instructions.readUint16(constIndexBytes);
                    ip += 2;
                    push(constants.get(constIndex));
                    break;

                case OP_ADD:
                case OP_SUB:
                case OP_MUL:
                case OP_DIV:
                    executeBinaryIntegerOperation(op);
                    break;

                case OP_TRUE:
                    push(TRUE);
                    break;

                case OP_FALSE:
                    push(FALSE);
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

                case OP_JUMP:
                    // 無條件跳轉
                    byte[] posBytes = new byte[2];
                    posBytes[0] = instructions.get(ip + 1);
                    posBytes[1] = instructions.get(ip + 2);
                    int pos = Instructions.readUint16(posBytes);
                    ip = pos - 1;
                    break;

                case OP_JUMP_NOT_TRUTHY:
                    // 條件跳轉
                    posBytes = new byte[2];
                    posBytes[0] = instructions.get(ip + 1);
                    posBytes[1] = instructions.get(ip + 2);
                    pos = Instructions.readUint16(posBytes);
                    ip += 2;

                    MonkeyObject condition = pop();
                    if (!isTruthy(condition)) {
                        ip = pos - 1;
                    }
                    break;

                case OP_NULL:
                    push(NULL);
                    break;

                case OP_SET_GLOBAL:
                    // Chapter 5: 設置全局變量
                    byte[] globalIndexBytes = new byte[2];
                    globalIndexBytes[0] = instructions.get(ip + 1);
                    globalIndexBytes[1] = instructions.get(ip + 2);
                    int globalIndex = Instructions.readUint16(globalIndexBytes);
                    ip += 2;

                    // 彈出堆疊頂部的值並存儲到全局變量
                    globals[globalIndex] = pop();
                    break;

                case OP_GET_GLOBAL:
                    // Chapter 5: 獲取全局變量
                    globalIndexBytes = new byte[2];
                    globalIndexBytes[0] = instructions.get(ip + 1);
                    globalIndexBytes[1] = instructions.get(ip + 2);
                    globalIndex = Instructions.readUint16(globalIndexBytes);
                    ip += 2;

                    // 從全局變量載入值並推入堆疊
                    push(globals[globalIndex]);
                    break;

                case OP_POP:
                    pop();
                    break;
            }
        }
    }

    /**
     * 執行二元整數運算
     */
    private void executeBinaryIntegerOperation(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        long leftValue = ((IntegerObject) left).getValue();
        long rightValue = ((IntegerObject) right).getValue();

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
            return;
        }

        switch (op) {
            case OP_EQUAL:
                push(nativeBoolToBooleanObject(left == right));
                break;
            case OP_NOT_EQUAL:
                push(nativeBoolToBooleanObject(left != right));
                break;
            default:
                throw new VMException(
                        String.format("unknown operator: %s (%s %s)",
                                op, left.type(), right.type())
                );
        }
    }

    /**
     * 執行整數比較
     */
    private void executeIntegerComparison(Opcode op, IntegerObject left, IntegerObject right)
            throws VMException {
        long leftValue = left.getValue();
        long rightValue = right.getValue();

        switch (op) {
            case OP_EQUAL:
                push(nativeBoolToBooleanObject(leftValue == rightValue));
                break;
            case OP_NOT_EQUAL:
                push(nativeBoolToBooleanObject(leftValue != rightValue));
                break;
            case OP_GREATER_THAN:
                push(nativeBoolToBooleanObject(leftValue > rightValue));
                break;
            default:
                throw new VMException("unknown operator: " + op);
        }
    }

    /**
     * 執行邏輯非運算
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
     * 執行取負運算
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
     * 判斷對象是否為真值
     */
    private boolean isTruthy(MonkeyObject obj) {
        if (obj == NULL) {
            return false;
        }
        if (obj == TRUE) {
            return true;
        }
        if (obj == FALSE) {
            return false;
        }
        return true;
    }

    /**
     * 將 Java boolean 轉換為 Monkey BooleanObject
     */
    private BooleanObject nativeBoolToBooleanObject(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * 推入堆疊
     */
    private void push(MonkeyObject obj) throws VMException {
        if (sp >= STACK_SIZE) {
            throw new VMException("stack overflow");
        }

        stack[sp] = obj;
        sp++;
    }

    /**
     * 從堆疊彈出
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
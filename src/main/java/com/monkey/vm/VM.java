package com.monkey.vm;

import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.compiler.Bytecode;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VM 是棧式虛擬機
 * Chapter 6: String, Array and Hash (擴展)
 *
 * 新增功能:
 * - 字串連接運算
 * - 陣列構建和索引
 * - 雜湊表構建和索引
 */
public class VM {
    private static final int STACK_SIZE = 2048;
    private static final int GLOBALS_SIZE = 65536;

    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);
    public static final NullObject NULL = new NullObject();

    private final List<MonkeyObject> constants;
    private final Instructions instructions;
    private final MonkeyObject[] stack;
    private int sp;
    private final MonkeyObject[] globals;

    public VM(Bytecode bytecode) {
        this(bytecode, new MonkeyObject[GLOBALS_SIZE]);
    }

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

    public MonkeyObject[] getGlobals() {
        return globals;
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
                    executeBinaryOperation(op);
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
                    byte[] posBytes = new byte[2];
                    posBytes[0] = instructions.get(ip + 1);
                    posBytes[1] = instructions.get(ip + 2);
                    int pos = Instructions.readUint16(posBytes);
                    ip = pos - 1;
                    break;

                case OP_JUMP_NOT_TRUTHY:
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
                    byte[] globalIndexBytes = new byte[2];
                    globalIndexBytes[0] = instructions.get(ip + 1);
                    globalIndexBytes[1] = instructions.get(ip + 2);
                    int globalIndex = Instructions.readUint16(globalIndexBytes);
                    ip += 2;
                    globals[globalIndex] = pop();
                    break;

                case OP_GET_GLOBAL:
                    globalIndexBytes = new byte[2];
                    globalIndexBytes[0] = instructions.get(ip + 1);
                    globalIndexBytes[1] = instructions.get(ip + 2);
                    globalIndex = Instructions.readUint16(globalIndexBytes);
                    ip += 2;
                    push(globals[globalIndex]);
                    break;

                // Chapter 6: 陣列指令
                case OP_ARRAY:
                    byte[] numElementsBytes = new byte[2];
                    numElementsBytes[0] = instructions.get(ip + 1);
                    numElementsBytes[1] = instructions.get(ip + 2);
                    int numElements = Instructions.readUint16(numElementsBytes);
                    ip += 2;

                    MonkeyObject array = buildArray(sp - numElements, sp);
                    sp = sp - numElements;
                    push(array);
                    break;

                // Chapter 6: 雜湊表指令
                case OP_HASH:
                    numElementsBytes = new byte[2];
                    numElementsBytes[0] = instructions.get(ip + 1);
                    numElementsBytes[1] = instructions.get(ip + 2);
                    numElements = Instructions.readUint16(numElementsBytes);
                    ip += 2;

                    MonkeyObject hash = buildHash(sp - numElements, sp);
                    sp = sp - numElements;
                    push(hash);
                    break;

                // Chapter 6: 索引指令
                case OP_INDEX:
                    MonkeyObject index = pop();
                    MonkeyObject left = pop();
                    executeIndexExpression(left, index);
                    break;

                case OP_POP:
                    pop();
                    break;
            }
        }
    }

    /**
     * Chapter 6: 構建陣列
     */
    private MonkeyObject buildArray(int startIndex, int endIndex) {
        List<MonkeyObject> elements = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            elements.add(stack[i]);
        }
        return new ArrayObject(elements);
    }

    /**
     * Chapter 6: 構建雜湊表
     */
    private MonkeyObject buildHash(int startIndex, int endIndex) throws VMException {
        Map<HashKey, HashObject.HashPair> hashedPairs = new HashMap<>();

        for (int i = startIndex; i < endIndex; i += 2) {
            MonkeyObject key = stack[i];
            MonkeyObject value = stack[i + 1];

            HashObject.HashPair pair = new HashObject.HashPair(key, value);

            if (!(key instanceof Hashable)) {
                throw new VMException("unusable as hash key: " + key.type());
            }

            Hashable hashKey = (Hashable) key;
            hashedPairs.put(hashKey.hashKey(), pair);
        }

        return new HashObject(hashedPairs);
    }

    /**
     * Chapter 6: 執行索引表達式
     */
    private void executeIndexExpression(MonkeyObject left, MonkeyObject index) throws VMException {
        if (left.type() == ObjectType.ARRAY && index.type() == ObjectType.INTEGER) {
            executeArrayIndex(left, index);
        } else if (left.type() == ObjectType.HASH) {
            executeHashIndex(left, index);
        } else {
            throw new VMException("index operator not supported: " + left.type());
        }
    }

    /**
     * Chapter 6: 執行陣列索引
     */
    private void executeArrayIndex(MonkeyObject array, MonkeyObject index) throws VMException {
        ArrayObject arrayObject = (ArrayObject) array;
        long i = ((IntegerObject) index).getValue();
        long max = arrayObject.getElements().size() - 1;

        if (i < 0 || i > max) {
            push(NULL);
            return;
        }

        push(arrayObject.getElements().get((int) i));
    }

    /**
     * Chapter 6: 執行雜湊表索引
     */
    private void executeHashIndex(MonkeyObject hash, MonkeyObject index) throws VMException {
        HashObject hashObject = (HashObject) hash;

        if (!(index instanceof Hashable)) {
            throw new VMException("unusable as hash key: " + index.type());
        }

        Hashable key = (Hashable) index;
        HashObject.HashPair pair = hashObject.getPairs().get(key.hashKey());

        if (pair == null) {
            push(NULL);
            return;
        }

        push(pair.value);
    }

    /**
     * Chapter 6: 執行二元運算（支援字串連接）
     */
    private void executeBinaryOperation(Opcode op) throws VMException {
        MonkeyObject right = pop();
        MonkeyObject left = pop();

        ObjectType leftType = left.type();
        ObjectType rightType = right.type();

        if (leftType == ObjectType.INTEGER && rightType == ObjectType.INTEGER) {
            executeBinaryIntegerOperation(op, left, right);
        } else if (leftType == ObjectType.STRING && rightType == ObjectType.STRING) {
            executeBinaryStringOperation(op, left, right);
        } else {
            throw new VMException(
                    String.format("unsupported types for binary operation: %s %s", leftType, rightType)
            );
        }
    }

    /**
     * 執行二元整數運算
     */
    private void executeBinaryIntegerOperation(Opcode op, MonkeyObject left, MonkeyObject right)
            throws VMException {
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
     * Chapter 6: 執行二元字串運算（字串連接）
     */
    private void executeBinaryStringOperation(Opcode op, MonkeyObject left, MonkeyObject right)
            throws VMException {
        if (op != Opcode.OP_ADD) {
            throw new VMException("unknown string operator: " + op);
        }

        String leftValue = ((StringObject) left).getValue();
        String rightValue = ((StringObject) right).getValue();

        push(new StringObject(leftValue + rightValue));
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

    private void executeMinusOperator() throws VMException {
        MonkeyObject operand = pop();

        if (!(operand instanceof IntegerObject)) {
            throw new VMException("unsupported type for negation: " + operand.type());
        }

        long value = ((IntegerObject) operand).getValue();
        push(new IntegerObject(-value));
    }

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

    private BooleanObject nativeBoolToBooleanObject(boolean value) {
        return value ? TRUE : FALSE;
    }

    private void push(MonkeyObject obj) throws VMException {
        if (sp >= STACK_SIZE) {
            throw new VMException("stack overflow");
        }

        stack[sp] = obj;
        sp++;
    }

    private MonkeyObject pop() {
        MonkeyObject o = stack[sp - 1];
        sp--;
        return o;
    }

    public static class VMException extends Exception {
        public VMException(String message) {
            super(message);
        }
    }
}
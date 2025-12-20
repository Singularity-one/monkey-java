package com.monkey.vm;

import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.compiler.Bytecode;
import com.monkey.object.*;

import java.util.List;

/**
 * VM 是棧式虛擬機
 * Chapter 9: Closures (擴展)
 */
public class VM {
    private static final int STACK_SIZE = 2048;
    private static final int GLOBALS_SIZE = 65536;
    private static final int MAX_FRAMES = 1024;

    public static final BooleanObject TRUE = new BooleanObject(true);
    public static final BooleanObject FALSE = new BooleanObject(false);
    public static final NullObject NULL = new NullObject();

    private final List<MonkeyObject> constants;
    private final MonkeyObject[] stack;
    private int sp;
    private final MonkeyObject[] globals;

    private final Frame[] frames;
    private int framesIndex;

    public VM(Bytecode bytecode) {
        this(bytecode, new MonkeyObject[GLOBALS_SIZE]);
    }

    public VM(Bytecode bytecode, MonkeyObject[] globals) {
        this.constants = bytecode.getConstants();
        this.stack = new MonkeyObject[STACK_SIZE];
        this.sp = 0;
        this.globals = globals;

        this.frames = new Frame[MAX_FRAMES];
        this.framesIndex = 1;

        // Chapter 9: 包裝主函數為閉包
        CompiledFunctionObject mainFn = new CompiledFunctionObject(bytecode.getInstructions());
        ClosureObject mainClosure = new ClosureObject(mainFn);
        Frame mainFrame = new Frame(mainClosure, 0);
        this.frames[0] = mainFrame;
    }

    private Frame currentFrame() {
        return frames[framesIndex - 1];
    }

    private void pushFrame(Frame f) {
        frames[framesIndex] = f;
        framesIndex++;
    }

    private Frame popFrame() {
        framesIndex--;
        return frames[framesIndex];
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

    public void run() throws VMException {
        int ip;
        Instructions ins;
        Opcode op;

        while (currentFrame().ip < currentFrame().instructions().size() - 1) {
            currentFrame().ip++;

            ip = currentFrame().ip;
            ins = currentFrame().instructions();
            op = Opcode.fromByte(ins.get(ip));

            switch (op) {
                case OP_CONSTANT:
                    int constIndex = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;
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
                    int pos = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip = pos - 1;
                    break;

                case OP_JUMP_NOT_TRUTHY:
                    pos = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;

                    MonkeyObject condition = pop();
                    if (!isTruthy(condition)) {
                        currentFrame().ip = pos - 1;
                    }
                    break;

                case OP_NULL:
                    push(NULL);
                    break;

                case OP_SET_GLOBAL:
                    int globalIndex = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;
                    globals[globalIndex] = pop();
                    break;

                case OP_GET_GLOBAL:
                    globalIndex = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;
                    push(globals[globalIndex]);
                    break;

                case OP_ARRAY:
                    int numElements = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;

                    MonkeyObject array = buildArray(sp - numElements, sp);
                    sp = sp - numElements;
                    push(array);
                    break;

                case OP_HASH:
                    numElements = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    currentFrame().ip += 2;

                    MonkeyObject hash = buildHash(sp - numElements, sp);
                    sp = sp - numElements;
                    push(hash);
                    break;

                case OP_INDEX:
                    MonkeyObject index = pop();
                    MonkeyObject left = pop();
                    executeIndexExpression(left, index);
                    break;

                case OP_CALL:
                    int numArgs = ins.get(ip + 1) & 0xFF;
                    currentFrame().ip += 1;
                    executeCall(numArgs);
                    break;

                case OP_RETURN_VALUE:
                    MonkeyObject returnValue = pop();

                    Frame frame = popFrame();
                    sp = frame.basePointer - 1;

                    push(returnValue);
                    break;

                case OP_RETURN:
                    frame = popFrame();
                    sp = frame.basePointer - 1;

                    push(NULL);
                    break;

                case OP_GET_LOCAL:
                    int localIndex = ins.get(ip + 1) & 0xFF;
                    currentFrame().ip += 1;

                    Frame cf = currentFrame();
                    push(stack[cf.basePointer + localIndex]);
                    break;

                case OP_SET_LOCAL:
                    localIndex = ins.get(ip + 1) & 0xFF;
                    currentFrame().ip += 1;

                    cf = currentFrame();
                    stack[cf.basePointer + localIndex] = pop();
                    break;

                case OP_GET_BUILTIN:
                    int builtinIndex = ins.get(ip + 1) & 0xFF;
                    currentFrame().ip += 1;

                    BuiltinObject builtin = Builtins.BUILTINS[builtinIndex].builtin;
                    push(builtin);
                    break;

                // Chapter 9: 創建閉包
                case OP_CLOSURE:
                    constIndex = Instructions.readUint16(new byte[]{
                            ins.get(ip + 1),
                            ins.get(ip + 2)
                    });
                    int numFree = ins.get(ip + 3) & 0xFF;
                    currentFrame().ip += 3;

                    pushClosure(constIndex, numFree);
                    break;

                // Chapter 9: 獲取自由變量
                case OP_GET_FREE:
                    int freeIndex = ins.get(ip + 1) & 0xFF;
                    currentFrame().ip += 1;

                    ClosureObject currentClosure = currentFrame().getClosure();
                    push(currentClosure.getFree()[freeIndex]);
                    break;

                case OP_POP:
                    pop();
                    break;
            }
        }
    }

    /**
     * Chapter 9: 創建閉包並推入堆疊
     */
    private void pushClosure(int constIndex, int numFree) throws VMException {
        MonkeyObject constant = constants.get(constIndex);
        if (!(constant instanceof CompiledFunctionObject)) {
            throw new VMException("not a function: " + constant);
        }

        CompiledFunctionObject function = (CompiledFunctionObject) constant;

        // 從堆疊收集自由變量
        MonkeyObject[] free = new MonkeyObject[numFree];
        for (int i = 0; i < numFree; i++) {
            free[i] = stack[sp - numFree + i];
        }
        sp = sp - numFree;

        // 創建閉包
        ClosureObject closure = new ClosureObject(function, free);
        push(closure);
    }

    /**
     * Chapter 9: 執行函數調用 (支持閉包)
     */
    private void executeCall(int numArgs) throws VMException {
        MonkeyObject callee = stack[sp - 1 - numArgs];

        if (callee instanceof ClosureObject) {
            callClosure((ClosureObject) callee, numArgs);
        } else if (callee instanceof BuiltinObject) {
            executeBuiltinFunction((BuiltinObject) callee, numArgs);
        } else {
            throw new VMException("calling non-closure and non-builtin");
        }
    }

    /**
     * Chapter 9: 調用閉包
     */
    private void callClosure(ClosureObject cl, int numArgs) throws VMException {
        if (numArgs != cl.getFn().getNumParameters()) {
            throw new VMException(
                    String.format("wrong number of arguments: want=%d, got=%d",
                            cl.getFn().getNumParameters(), numArgs)
            );
        }

        Frame frame = new Frame(cl, sp - numArgs);
        pushFrame(frame);

        sp = frame.basePointer + cl.getFn().getNumLocals();
    }

    private void executeBuiltinFunction(BuiltinObject builtin, int numArgs) throws VMException {
        MonkeyObject[] args = new MonkeyObject[numArgs];
        for (int i = 0; i < numArgs; i++) {
            args[i] = stack[sp - numArgs + i];
        }

        MonkeyObject result = builtin.getFn().apply(args);

        sp = sp - numArgs - 1;

        if (result != null) {
            push(result);
        } else {
            push(NULL);
        }
    }

    // ... 其他方法保持不變 ...

    private MonkeyObject buildArray(int startIndex, int endIndex) {
        java.util.List<MonkeyObject> elements = new java.util.ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            elements.add(stack[i]);
        }
        return new ArrayObject(elements);
    }

    private MonkeyObject buildHash(int startIndex, int endIndex) throws VMException {
        java.util.Map<HashKey, HashObject.HashPair> hashedPairs = new java.util.HashMap<>();

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

    private void executeIndexExpression(MonkeyObject left, MonkeyObject index) throws VMException {
        if (left.type() == ObjectType.ARRAY && index.type() == ObjectType.INTEGER) {
            executeArrayIndex(left, index);
        } else if (left.type() == ObjectType.HASH) {
            executeHashIndex(left, index);
        } else {
            throw new VMException("index operator not supported: " + left.type());
        }
    }

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

    private void executeBinaryStringOperation(Opcode op, MonkeyObject left, MonkeyObject right)
            throws VMException {
        if (op != Opcode.OP_ADD) {
            throw new VMException("unknown string operator: " + op);
        }

        String leftValue = ((StringObject) left).getValue();
        String rightValue = ((StringObject) right).getValue();

        push(new StringObject(leftValue + rightValue));
    }

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
package com.monkey.vm;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.compiler.Bytecode;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;

import java.util.List;

/**
 * VM 是棧式虛擬機
 *
 * 虛擬機的職責:
 * 1. 取指 (Fetch) - 讀取下一條指令
 * 2. 解碼 (Decode) - 解析指令和操作數
 * 3. 執行 (Execute) - 執行指令操作
 *
 * 堆疊機架構:
 * - 使用堆疊存儲中間值
 * - 操作數從堆疊彈出
 * - 結果推入堆疊
 *
 * Chapter 2: Hello Bytecode!
 */
public class VM {
    // 堆疊大小: 2048 個元素應該足夠用了
    private static final int STACK_SIZE = 2048;

    private final List<MonkeyObject> constants;    // 常量池 (來自編譯器)
    private final Instructions instructions;        // 字節碼指令 (來自編譯器)

    private final MonkeyObject[] stack;            // 堆疊
    private int sp;                                 // 堆疊指針

    /**
     * 堆疊指針約定:
     * - sp 始終指向下一個空閒位置
     * - 堆疊頂部元素在 stack[sp-1]
     * - 空堆疊時 sp = 0
     *
     * 例如:
     * sp=0: []
     * sp=1: [5]
     * sp=2: [5, 10]
     */

    public VM(Bytecode bytecode) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants();
        this.stack = new MonkeyObject[STACK_SIZE];
        this.sp = 0;
    }

    /**
     * 獲取堆疊頂部元素
     *
     * 用於測試和 REPL 中獲取執行結果
     */
    public MonkeyObject stackTop() {
        if (sp == 0) {
            return null;
        }
        return stack[sp - 1];
    }

    /**
     * 獲取最後彈出的元素
     *
     * Chapter 3 需要用到
     */
    public MonkeyObject lastPoppedStackElem() {
        return stack[sp];
    }

    /**
     * 運行虛擬機 - 取指-解碼-執行循環
     *
     * 這是 VM 的心臟!
     */
    public void run() throws VMException {
        // ip: instruction pointer (指令指針)
        // 指向當前正在執行的指令
        for (int ip = 0; ip < instructions.size(); ip++) {
            // ===== 取指 (Fetch) =====
            // 讀取當前位置的操作碼
            byte opByte = instructions.get(ip);
            Opcode op = Opcode.fromByte(opByte);

            // ===== 解碼和執行 (Decode & Execute) =====
            switch (op) {
                case OP_CONSTANT:
                    // 1. 取得操作碼定義
                    Instructions.Definition def = Instructions.lookup(opByte);

                    // 2. 從當前 ip+1 取剩餘字節
                    byte[] insSlice = new byte[instructions.size() - (ip + 1)];
                    for (int j = 0; j < insSlice.length; j++) {
                        insSlice[j] = instructions.get(ip + 1 + j);
                    }

                    // 3. 解析操作數
                    Instructions.ReadOperandsResult operandsRead = Instructions.readOperands(def, insSlice);
                    int constIndex = operandsRead.operands[0];

                    // 4. ip 移動過操作數
                    ip += operandsRead.bytesRead;

                    // 5. 推入堆疊
                    MonkeyObject constant = constants.get(constIndex);
                    if (constant == null) {
                        throw new VMException("constant at index " + constIndex + " is null");
                    }
                    push(constant);
                    break;

                case OP_ADD:
                    // OpAdd: 加法運算
                    // 堆疊: [... left, right] -> [... result]

                    // 1. 彈出右操作數
                    MonkeyObject right = pop();
                    // 2. 彈出左操作數
                    MonkeyObject left = pop();

                    // 3. 提取整數值
                    long leftValue = ((IntegerObject) left).getValue();
                    long rightValue = ((IntegerObject) right).getValue();

                    // 4. 執行加法
                    long result = leftValue + rightValue;

                    // 5. 將結果推入堆疊
                    push(new IntegerObject(result));
                    break;

                case OP_SUB:
                    // OpSub: 減法運算
                    right = pop();
                    left = pop();
                    leftValue = ((IntegerObject) left).getValue();
                    rightValue = ((IntegerObject) right).getValue();
                    result = leftValue - rightValue;
                    push(new IntegerObject(result));
                    break;

                case OP_MUL:
                    // OpMul: 乘法運算
                    right = pop();
                    left = pop();
                    leftValue = ((IntegerObject) left).getValue();
                    rightValue = ((IntegerObject) right).getValue();
                    result = leftValue * rightValue;
                    push(new IntegerObject(result));
                    break;

                case OP_DIV:
                    // OpDiv: 除法運算
                    right = pop();
                    left = pop();
                    leftValue = ((IntegerObject) left).getValue();
                    rightValue = ((IntegerObject) right).getValue();

                    // 檢查除零錯誤
                    if (rightValue == 0) {
                        throw new VMException("division by zero");
                    }

                    result = leftValue / rightValue;
                    push(new IntegerObject(result));
                    break;

                case OP_POP:
                    // OpPop: 彈出堆疊頂部元素
                    // Chapter 3 會用到
                    pop();
                    break;
            }
        }
    }

    /**
     * 將對象推入堆疊
     *
     * @param obj 要推入的對象
     * @throws VMException 如果堆疊溢出
     */
    private void push(MonkeyObject obj) throws VMException {
        if (sp >= STACK_SIZE) {
            throw new VMException("stack overflow");
        }

        stack[sp] = obj;
        System.out.println("Pushing: " + obj + " at sp=" + sp);  // <- debug
        sp++;
    }

    /**
     * 從堆疊彈出對象
     *
     * @return 堆疊頂部的對象
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

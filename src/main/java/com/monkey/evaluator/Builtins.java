package com.monkey.evaluator;
import com.monkey.object.*;

import java.util.*;

/**
 * Builtins 定義所有內建函數
 */
public class Builtins {

    private static final Map<String, BuiltinFunction> BUILTINS = new HashMap<>();

    static {
        // len(arg) - 返回長度
        BUILTINS.put("len", new BuiltinFunction(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=1");
            }

            MonkeyObject arg = args.get(0);
            if (arg instanceof StringObject str) {
                return new IntegerObject(str.getValue().length());
            } else if (arg instanceof ArrayObject arr) {
                return new IntegerObject(arr.getElements().size());
            } else {
                return new ErrorObject("argument to `len` not supported, got " + arg.type());
            }
        }));

        // first(array) - 返回第一個元素
        BUILTINS.put("first", new BuiltinFunction(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=1");
            }

            if (!(args.get(0) instanceof ArrayObject arr)) {
                return new ErrorObject("argument to `first` must be ARRAY, got " + args.get(0).type());
            }

            if (arr.getElements().isEmpty()) {
                return NullObject.NULL;
            }

            return arr.getElements().get(0);
        }));

        // last(array) - 返回最後一個元素
        BUILTINS.put("last", new BuiltinFunction(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=1");
            }

            if (!(args.get(0) instanceof ArrayObject arr)) {
                return new ErrorObject("argument to `last` must be ARRAY, got " + args.get(0).type());
            }

            if (arr.getElements().isEmpty()) {
                return NullObject.NULL;
            }

            return arr.getElements().get(arr.getElements().size() - 1);
        }));

        // rest(array) - 返回除第一個外的所有元素
        BUILTINS.put("rest", new BuiltinFunction(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=1");
            }

            if (!(args.get(0) instanceof ArrayObject arr)) {
                return new ErrorObject("argument to `rest` must be ARRAY, got " + args.get(0).type());
            }

            if (arr.getElements().isEmpty()) {
                return NullObject.NULL;
            }

            List<MonkeyObject> newElements = new ArrayList<>(arr.getElements().subList(1, arr.getElements().size()));
            return new ArrayObject(newElements);
        }));

        // push(array, element) - 添加元素到陣列末尾
        BUILTINS.put("push", new BuiltinFunction(args -> {
            if (args.size() != 2) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=2");
            }

            if (!(args.get(0) instanceof ArrayObject arr)) {
                return new ErrorObject("argument to `push` must be ARRAY, got " + args.get(0).type());
            }

            List<MonkeyObject> newElements = new ArrayList<>(arr.getElements());
            newElements.add(args.get(1));
            return new ArrayObject(newElements);
        }));

        // puts(...args) - 打印輸出
        BUILTINS.put("puts", new BuiltinFunction(args -> {
            for (MonkeyObject arg : args) {
                System.out.println(arg.inspect());
            }
            return NullObject.NULL;
        }));
    }

    /**
     * 獲取內建函數
     */
    public static BuiltinFunction getBuiltin(String name) {
        return BUILTINS.get(name);
    }
}

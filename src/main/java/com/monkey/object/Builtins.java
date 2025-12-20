package com.monkey.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Builtins 包含所有內建函數的定義
 * Chapter 8: Built-in Functions
 */
public class Builtins {

    public static class BuiltinDefinition {
        public final String name;
        public final BuiltinObject builtin;

        public BuiltinDefinition(String name, BuiltinObject builtin) {
            this.name = name;
            this.builtin = builtin;
        }
    }

    /**
     * 所有內建函數的列表
     * 索引順序很重要，用於 OpGetBuiltin 指令
     */
    public static final BuiltinDefinition[] BUILTINS = new BuiltinDefinition[]{
            // 0: len
            new BuiltinDefinition("len", new BuiltinObject(args -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }

                if (args[0] instanceof ArrayObject) {
                    ArrayObject arr = (ArrayObject) args[0];
                    return new IntegerObject(arr.getElements().size());
                } else if (args[0] instanceof StringObject) {
                    StringObject str = (StringObject) args[0];
                    return new IntegerObject(str.getValue().length());
                } else {
                    return newError("argument to `len` not supported, got %s", args[0].type());
                }
            })),

            // 1: puts
            new BuiltinDefinition("puts", new BuiltinObject(args -> {
                for (MonkeyObject arg : args) {
                    System.out.println(arg.inspect());
                }
                return null; // 返回 null，VM 會轉換為 NULL
            })),

            // 2: first
            new BuiltinDefinition("first", new BuiltinObject(args -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (!(args[0] instanceof ArrayObject)) {
                    return newError("argument to `first` must be ARRAY, got %s", args[0].type());
                }

                ArrayObject arr = (ArrayObject) args[0];
                if (arr.getElements().size() > 0) {
                    return arr.getElements().get(0);
                }
                return null; // 返回 null，VM 會轉換為 NULL
            })),

            // 3: last
            new BuiltinDefinition("last", new BuiltinObject(args -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (!(args[0] instanceof ArrayObject)) {
                    return newError("argument to `last` must be ARRAY, got %s", args[0].type());
                }

                ArrayObject arr = (ArrayObject) args[0];
                int length = arr.getElements().size();
                if (length > 0) {
                    return arr.getElements().get(length - 1);
                }
                return null; // 返回 null，VM 會轉換為 NULL
            })),

            // 4: rest
            new BuiltinDefinition("rest", new BuiltinObject(args -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (!(args[0] instanceof ArrayObject)) {
                    return newError("argument to `rest` must be ARRAY, got %s", args[0].type());
                }

                ArrayObject arr = (ArrayObject) args[0];
                int length = arr.getElements().size();
                if (length > 0) {
                    List<MonkeyObject> newElements = new ArrayList<>(arr.getElements().subList(1, length));
                    return new ArrayObject(newElements);
                }
                return null; // 返回 null，VM 會轉換為 NULL
            })),

            // 5: push
            new BuiltinDefinition("push", new BuiltinObject(args -> {
                if (args.length != 2) {
                    return newError("wrong number of arguments. got=%d, want=2", args.length);
                }
                if (!(args[0] instanceof ArrayObject)) {
                    return newError("argument to `push` must be ARRAY, got %s", args[0].type());
                }

                ArrayObject arr = (ArrayObject) args[0];
                List<MonkeyObject> newElements = new ArrayList<>(arr.getElements());
                newElements.add(args[1]);
                return new ArrayObject(newElements);
            }))
    };

    /**
     * 根據名稱獲取內建函數
     */
    public static BuiltinObject getBuiltinByName(String name) {
        for (BuiltinDefinition def : BUILTINS) {
            if (def.name.equals(name)) {
                return def.builtin;
            }
        }
        return null;
    }

    /**
     * 創建錯誤對象
     */
    private static ErrorObject newError(String format, Object... args) {
        return new ErrorObject(String.format(format, args));
    }
}
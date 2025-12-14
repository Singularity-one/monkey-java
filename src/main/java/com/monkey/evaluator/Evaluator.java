package com.monkey.evaluator;
import com.monkey.ast.*;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluator 遍歷 AST 並對其求值
 * 實現 Tree-Walking Interpreter
 */
public class Evaluator {

    // 單例 NULL 和 BOOLEAN 物件
    private static final NullObject NULL = NullObject.NULL;
    private static final BooleanObject TRUE = BooleanObject.TRUE;
    private static final BooleanObject FALSE = BooleanObject.FALSE;

    /**
     * 求值節點
     */
    public static MonkeyObject eval(Node node, Environment env) {
        // Program - 求值所有語句
        if (node instanceof Program program) {
            return evalProgram(program, env);
        }

        // 語句
        if (node instanceof ExpressionStatement stmt) {
            return eval(stmt.getExpression(), env);
        }

        if (node instanceof BlockStatement block) {
            return evalBlockStatement(block, env);
        }

        if (node instanceof ReturnStatement returnStmt) {
            MonkeyObject val = eval(returnStmt.getReturnValue(), env);
            if (isError(val)) {
                return val;
            }
            return new ReturnValue(val);
        }

        if (node instanceof LetStatement letStmt) {
            MonkeyObject val = eval(letStmt.getValue(), env);
            if (isError(val)) {
                return val;
            }
            env.set(letStmt.getName().getValue(), val);
            return val;
        }

        // 表達式
        if (node instanceof IntegerLiteral intLit) {
            return new IntegerObject(intLit.getValue());
        }

        if (node instanceof BooleanLiteral boolLit) {
            return nativeBoolToBooleanObject(boolLit.getValue());
        }

        if (node instanceof PrefixExpression prefix) {
            MonkeyObject right = eval(prefix.getRight(), env);
            if (isError(right)) {
                return right;
            }
            return evalPrefixExpression(prefix.getOperator(), right);
        }

        if (node instanceof InfixExpression infix) {
            MonkeyObject left = eval(infix.getLeft(), env);
            if (isError(left)) {
                return left;
            }

            MonkeyObject right = eval(infix.getRight(), env);
            if (isError(right)) {
                return right;
            }

            return evalInfixExpression(infix.getOperator(), left, right);
        }

        if (node instanceof IfExpression ifExpr) {
            return evalIfExpression(ifExpr, env);
        }

        if (node instanceof Identifier ident) {
            return evalIdentifier(ident, env);
        }

        if (node instanceof FunctionLiteral fn) {
            return new FunctionObject(fn.getParameters(), fn.getBody(), env);
        }

        if (node instanceof CallExpression call) {
            MonkeyObject function = eval(call.getFunction(), env);
            if (isError(function)) {
                return function;
            }

            List<MonkeyObject> args = evalExpressions(call.getArguments(), env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }

            return applyFunction(function, args);
        }

        return null;
    }

    /**
     * 求值程式（所有語句）
     */
    private static MonkeyObject evalProgram(Program program, Environment env) {
        MonkeyObject result = null;

        for (Statement stmt : program.getStatements()) {
            result = eval(stmt, env);

            // 遇到 return，立即返回值
            if (result instanceof ReturnValue returnValue) {
                return returnValue.getValue();
            }

            // 遇到錯誤，立即返回
            if (result instanceof ErrorObject) {
                return result;
            }
        }

        return result;
    }

    /**
     * 求值區塊語句
     */
    private static MonkeyObject evalBlockStatement(BlockStatement block, Environment env) {
        MonkeyObject result = null;

        for (Statement stmt : block.getStatements()) {
            result = eval(stmt, env);

            if (result != null) {
                ObjectType type = result.type();
                // 不解包 ReturnValue，讓它向上傳播
                if (type == ObjectType.RETURN_VALUE || type == ObjectType.ERROR) {
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * 求值前綴表達式
     */
    private static MonkeyObject evalPrefixExpression(String operator, MonkeyObject right) {
        return switch (operator) {
            case "!" -> evalBangOperatorExpression(right);
            case "-" -> evalMinusPrefixOperatorExpression(right);
            default -> newError("unknown operator: %s%s", operator, right.type());
        };
    }

    /**
     * 求值 ! 運算符
     */
    private static MonkeyObject evalBangOperatorExpression(MonkeyObject right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * 求值 - 運算符
     */
    private static MonkeyObject evalMinusPrefixOperatorExpression(MonkeyObject right) {
        if (right.type() != ObjectType.INTEGER) {
            return newError("unknown operator: -%s", right.type());
        }

        long value = ((IntegerObject) right).getValue();
        return new IntegerObject(-value);
    }

    /**
     * 求值中綴表達式
     */
    private static MonkeyObject evalInfixExpression(String operator, MonkeyObject left, MonkeyObject right) {
        // 整數運算
        if (left.type() == ObjectType.INTEGER && right.type() == ObjectType.INTEGER) {
            return evalIntegerInfixExpression(operator, left, right);
        }

        // 布林運算（使用物件相等性）
        if (operator.equals("==")) {
            return nativeBoolToBooleanObject(left == right);
        }
        if (operator.equals("!=")) {
            return nativeBoolToBooleanObject(left != right);
        }

        // 類型不匹配
        if (left.type() != right.type()) {
            return newError("type mismatch: %s %s %s", left.type(), operator, right.type());
        }

        return newError("unknown operator: %s %s %s", left.type(), operator, right.type());
    }

    /**
     * 求值整數中綴表達式
     */
    private static MonkeyObject evalIntegerInfixExpression(String operator, MonkeyObject left, MonkeyObject right) {
        long leftVal = ((IntegerObject) left).getValue();
        long rightVal = ((IntegerObject) right).getValue();

        return switch (operator) {
            case "+" -> new IntegerObject(leftVal + rightVal);
            case "-" -> new IntegerObject(leftVal - rightVal);
            case "*" -> new IntegerObject(leftVal * rightVal);
            case "/" -> {
                if (rightVal == 0) {
                    yield newError("division by zero");
                }
                yield new IntegerObject(leftVal / rightVal);
            }
            case "<" -> nativeBoolToBooleanObject(leftVal < rightVal);
            case ">" -> nativeBoolToBooleanObject(leftVal > rightVal);
            case "==" -> nativeBoolToBooleanObject(leftVal == rightVal);
            case "!=" -> nativeBoolToBooleanObject(leftVal != rightVal);
            default -> newError("unknown operator: %s %s %s", left.type(), operator, right.type());
        };
    }

    /**
     * 求值 if 表達式
     */
    private static MonkeyObject evalIfExpression(IfExpression ie, Environment env) {
        MonkeyObject condition = eval(ie.getCondition(), env);
        if (isError(condition)) {
            return condition;
        }

        if (isTruthy(condition)) {
            return eval(ie.getConsequence(), env);
        } else if (ie.getAlternative() != null) {
            return eval(ie.getAlternative(), env);
        } else {
            return NULL;
        }
    }

    /**
     * 求值識別符號
     */
    private static MonkeyObject evalIdentifier(Identifier node, Environment env) {
        MonkeyObject val = env.get(node.getValue());
        if (val == null) {
            return newError("identifier not found: " + node.getValue());
        }
        return val;
    }

    /**
     * 求值表達式列表
     */
    private static List<MonkeyObject> evalExpressions(List<Expression> exps, Environment env) {
        List<MonkeyObject> result = new ArrayList<>();

        for (Expression e : exps) {
            MonkeyObject evaluated = eval(e, env);
            if (isError(evaluated)) {
                return List.of(evaluated);
            }
            result.add(evaluated);
        }

        return result;
    }

    /**
     * 應用函數
     */
    private static MonkeyObject applyFunction(MonkeyObject fn, List<MonkeyObject> args) {
        if (!(fn instanceof FunctionObject function)) {
            return newError("not a function: %s", fn.type());
        }

        Environment extendedEnv = extendFunctionEnv(function, args);
        MonkeyObject evaluated = eval(function.getBody(), extendedEnv);
        return unwrapReturnValue(evaluated);
    }

    /**
     * 擴展函數環境（綁定參數）
     */
    private static Environment extendFunctionEnv(FunctionObject fn, List<MonkeyObject> args) {
        Environment env = Environment.newEnclosedEnvironment(fn.getEnv());

        List<Identifier> params = fn.getParameters();
        for (int i = 0; i < params.size(); i++) {
            env.set(params.get(i).getValue(), args.get(i));
        }

        return env;
    }

    /**
     * 解包 ReturnValue
     */
    private static MonkeyObject unwrapReturnValue(MonkeyObject obj) {
        if (obj instanceof ReturnValue returnValue) {
            return returnValue.getValue();
        }
        return obj;
    }

    // 輔助方法

    /**
     * 判斷值是否為真
     * Monkey 中，只有 false 和 null 是假值
     */
    private static boolean isTruthy(MonkeyObject obj) {
        if (obj == NULL) {
            return false;
        } else if (obj == TRUE) {
            return true;
        } else if (obj == FALSE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 將 Java boolean 轉換為 BooleanObject
     */
    private static BooleanObject nativeBoolToBooleanObject(boolean input) {
        return input ? TRUE : FALSE;
    }

    /**
     * 創建錯誤物件
     */
    private static ErrorObject newError(String format, Object... args) {
        return new ErrorObject(String.format(format, args));
    }

    /**
     * 判斷是否為錯誤物件
     */
    private static boolean isError(MonkeyObject obj) {
        return obj != null && obj.type() == ObjectType.ERROR;
    }
}

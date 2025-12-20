package com.monkey.compiler;

import com.monkey.ast.*;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.object.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Compiler 將 AST 編譯為字節碼
 * Chapter 9: Closures (擴展)
 */
public class Compiler {
    private final List<MonkeyObject> constants;
    private SymbolTable symbolTable;

    private final List<CompilationScope> scopes;
    private int scopeIndex;

    public Compiler() {
        this.constants = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        this.scopes = new ArrayList<>();
        this.scopeIndex = 0;

        // 定義所有內建函數
        for (int i = 0; i < Builtins.BUILTINS.length; i++) {
            symbolTable.defineBuiltin(i, Builtins.BUILTINS[i].name);
        }

        CompilationScope mainScope = new CompilationScope();
        scopes.add(mainScope);
    }

    public Compiler(SymbolTable symbolTable, List<MonkeyObject> constants) {
        this.constants = constants;
        this.symbolTable = symbolTable;
        this.scopes = new ArrayList<>();
        this.scopeIndex = 0;

        CompilationScope mainScope = new CompilationScope();
        scopes.add(mainScope);
    }

    private void enterScope() {
        CompilationScope scope = new CompilationScope();
        scopes.add(scope);
        scopeIndex++;

        symbolTable = SymbolTable.newEnclosed(symbolTable);
    }

    private Instructions leaveScope() {
        Instructions instructions = currentInstructions();
        scopes.remove(scopes.size() - 1);
        scopeIndex--;

        symbolTable = symbolTable.getOuter();

        return instructions;
    }

    private Instructions currentInstructions() {
        return scopes.get(scopeIndex).getInstructions();
    }

    public void compile(Node node) throws CompilerException {
        if (node instanceof Program) {
            Program program = (Program) node;
            for (Statement stmt : program.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof ExpressionStatement) {
            ExpressionStatement exprStmt = (ExpressionStatement) node;
            compile(exprStmt.getExpression());
            emit(Opcode.OP_POP);
        }
        else if (node instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) node;
            for (Statement stmt : block.getStatements()) {
                compile(stmt);
            }
        }
        else if (node instanceof LetStatement) {
            compileLetStatement((LetStatement) node);
        }
        else if (node instanceof ReturnStatement) {
            compileReturnStatement((ReturnStatement) node);
        }
        else if (node instanceof IfExpression) {
            compileIfExpression((IfExpression) node);
        }
        else if (node instanceof InfixExpression) {
            InfixExpression infixExpr = (InfixExpression) node;

            if (infixExpr.getOperator().equals("<")) {
                compile(infixExpr.getRight());
                compile(infixExpr.getLeft());
                emit(Opcode.OP_GREATER_THAN);
                return;
            }

            compile(infixExpr.getLeft());
            compile(infixExpr.getRight());

            switch (infixExpr.getOperator()) {
                case "+":
                    emit(Opcode.OP_ADD);
                    break;
                case "-":
                    emit(Opcode.OP_SUB);
                    break;
                case "*":
                    emit(Opcode.OP_MUL);
                    break;
                case "/":
                    emit(Opcode.OP_DIV);
                    break;
                case ">":
                    emit(Opcode.OP_GREATER_THAN);
                    break;
                case "==":
                    emit(Opcode.OP_EQUAL);
                    break;
                case "!=":
                    emit(Opcode.OP_NOT_EQUAL);
                    break;
                default:
                    throw new CompilerException("unknown operator " + infixExpr.getOperator());
            }
        }
        else if (node instanceof PrefixExpression) {
            PrefixExpression prefixExpr = (PrefixExpression) node;
            compile(prefixExpr.getRight());

            switch (prefixExpr.getOperator()) {
                case "!":
                    emit(Opcode.OP_BANG);
                    break;
                case "-":
                    emit(Opcode.OP_MINUS);
                    break;
                default:
                    throw new CompilerException("unknown operator " + prefixExpr.getOperator());
            }
        }
        else if (node instanceof IntegerLiteral) {
            IntegerLiteral intLit = (IntegerLiteral) node;
            IntegerObject integer = new IntegerObject(intLit.getValue());
            emit(Opcode.OP_CONSTANT, addConstant(integer));
        }
        else if (node instanceof StringLiteral) {
            StringLiteral strLit = (StringLiteral) node;
            StringObject str = new StringObject(strLit.getValue());
            emit(Opcode.OP_CONSTANT, addConstant(str));
        }
        else if (node instanceof BooleanLiteral) {
            BooleanLiteral boolLit = (BooleanLiteral) node;
            if (boolLit.getValue()) {
                emit(Opcode.OP_TRUE);
            } else {
                emit(Opcode.OP_FALSE);
            }
        }
        else if (node instanceof ArrayLiteral) {
            compileArrayLiteral((ArrayLiteral) node);
        }
        else if (node instanceof HashLiteral) {
            compileHashLiteral((HashLiteral) node);
        }
        else if (node instanceof IndexExpression) {
            compileIndexExpression((IndexExpression) node);
        }
        else if (node instanceof FunctionLiteral) {
            compileFunctionLiteral((FunctionLiteral) node);
        }
        else if (node instanceof CallExpression) {
            compileCallExpression((CallExpression) node);
        }
        else if (node instanceof Identifier) {
            compileIdentifier((Identifier) node);
        }
    }

    /**
     * Chapter 9: 編譯函數字面量 (支持閉包)
     */
    private void compileFunctionLiteral(FunctionLiteral fn) throws CompilerException {
        enterScope();

        for (Identifier param : fn.getParameters()) {
            symbolTable.define(param.getValue());
        }

        compile(fn.getBody());

        if (lastInstructionIs(Opcode.OP_POP)) {
            replaceLastPopWithReturn();
        }
        if (!lastInstructionIs(Opcode.OP_RETURN_VALUE)) {
            emit(Opcode.OP_RETURN);
        }

        // Chapter 9: 獲取自由變量
        List<Symbol> freeSymbols = symbolTable.getFreeSymbols();
        int numLocals = symbolTable.getNumDefinitions();

        Instructions instructions = leaveScope();

        // Chapter 9: 載入自由變量到堆疊
        for (Symbol s : freeSymbols) {
            loadSymbol(s);
        }

        CompiledFunctionObject compiledFn = new CompiledFunctionObject(
                instructions,
                numLocals,
                fn.getParameters().size()
        );

        int fnIndex = addConstant(compiledFn);

        // Chapter 9: 發射 OpClosure 指令
        emit(Opcode.OP_CLOSURE, fnIndex, freeSymbols.size());
    }

    private void replaceLastPopWithReturn() {
        int lastPos = scopes.get(scopeIndex).getLastInstruction().getPosition();
        replaceInstruction(lastPos, Instructions.make(Opcode.OP_RETURN_VALUE));
        scopes.get(scopeIndex).getLastInstruction().setOpcode(Opcode.OP_RETURN_VALUE);
    }

    private void replaceInstruction(int pos, byte[] newInstruction) {
        Instructions ins = currentInstructions();
        for (int i = 0; i < newInstruction.length; i++) {
            ins.set(pos + i, newInstruction[i]);
        }
    }

    private void compileCallExpression(CallExpression call) throws CompilerException {
        compile(call.getFunction());

        for (Expression arg : call.getArguments()) {
            compile(arg);
        }

        emit(Opcode.OP_CALL, call.getArguments().size());
    }

    private void compileReturnStatement(ReturnStatement returnStmt) throws CompilerException {
        compile(returnStmt.getReturnValue());
        emit(Opcode.OP_RETURN_VALUE);
    }

    private void compileLetStatement(LetStatement letStmt) throws CompilerException {
        Symbol symbol = symbolTable.define(letStmt.getName().getValue());

        // 然後編譯右側的值
        compile(letStmt.getValue());

        // 最後發射賦值指令
        if (symbol.getScope() == SymbolScope.GLOBAL) {
            emit(Opcode.OP_SET_GLOBAL, symbol.getIndex());
        } else {
            emit(Opcode.OP_SET_LOCAL, symbol.getIndex());
        }
    }

    private void compileIdentifier(Identifier ident) throws CompilerException {
        Symbol symbol = symbolTable.resolve(ident.getValue());
        if (symbol == null) {
            throw new CompilerException("undefined variable " + ident.getValue());
        }

        loadSymbol(symbol);
    }

    /**
     * Chapter 9: 根據符號作用域載入符號 (支持自由變量)
     */
    private void loadSymbol(Symbol symbol) {
        switch (symbol.getScope()) {
            case GLOBAL:
                emit(Opcode.OP_GET_GLOBAL, symbol.getIndex());
                break;
            case LOCAL:
                emit(Opcode.OP_GET_LOCAL, symbol.getIndex());
                break;
            case BUILTIN:
                emit(Opcode.OP_GET_BUILTIN, symbol.getIndex());
                break;
            case FREE:
                emit(Opcode.OP_GET_FREE, symbol.getIndex());
                break;
        }
    }

    private void compileArrayLiteral(ArrayLiteral array) throws CompilerException {
        for (Expression element : array.getElements()) {
            compile(element);
        }
        emit(Opcode.OP_ARRAY, array.getElements().size());
    }

    private void compileHashLiteral(HashLiteral hash) throws CompilerException {
        List<Expression> keys = new ArrayList<>(hash.getPairs().keySet());
        keys.sort(Comparator.comparing(Node::string));

        for (Expression key : keys) {
            compile(key);
            compile(hash.getPairs().get(key));
        }

        emit(Opcode.OP_HASH, hash.getPairs().size() * 2);
    }

    private void compileIndexExpression(IndexExpression indexExpr) throws CompilerException {
        compile(indexExpr.getLeft());
        compile(indexExpr.getIndex());
        emit(Opcode.OP_INDEX);
    }

    private void compileIfExpression(IfExpression ifExpr) throws CompilerException {
        compile(ifExpr.getCondition());

        int jumpNotTruthyPos = emit(Opcode.OP_JUMP_NOT_TRUTHY, 9999);

        compile(ifExpr.getConsequence());

        if (lastInstructionIs(Opcode.OP_POP)) {
            removeLastPop();
        }

        int jumpPos = emit(Opcode.OP_JUMP, 9999);

        int afterConsequencePos = currentInstructions().size();
        changeOperand(jumpNotTruthyPos, afterConsequencePos);

        if (ifExpr.getAlternative() == null) {
            emit(Opcode.OP_NULL);
        } else {
            compile(ifExpr.getAlternative());

            if (lastInstructionIs(Opcode.OP_POP)) {
                removeLastPop();
            }
        }

        int afterAlternativePos = currentInstructions().size();
        changeOperand(jumpPos, afterAlternativePos);
    }

    private int emit(Opcode op, int... operands) {
        byte[] ins = Instructions.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op, pos);
        return pos;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = currentInstructions().size();
        currentInstructions().append(ins);
        return posNewInstruction;
    }

    private void setLastInstruction(Opcode op, int pos) {
        CompilationScope scope = scopes.get(scopeIndex);
        scope.setPreviousInstruction(scope.getLastInstruction());
        scope.setLastInstruction(new EmittedInstruction(op, pos));
    }

    private boolean lastInstructionIs(Opcode op) {
        if (currentInstructions().size() == 0) {
            return false;
        }
        EmittedInstruction last = scopes.get(scopeIndex).getLastInstruction();
        return last != null && last.getOpcode() == op;
    }

    private void removeLastPop() {
        CompilationScope scope = scopes.get(scopeIndex);
        EmittedInstruction last = scope.getLastInstruction();
        if (last != null && last.getOpcode() == Opcode.OP_POP) {
            currentInstructions().removeLast(1);
            scope.setLastInstruction(scope.getPreviousInstruction());
        }
    }

    private void changeOperand(int opPos, int operand) {
        currentInstructions().changeOperand(opPos, operand);
    }

    private int addConstant(MonkeyObject obj) {
        constants.add(obj);
        return constants.size() - 1;
    }

    public Bytecode bytecode() {
        return new Bytecode(currentInstructions(), constants);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static class CompilerException extends Exception {
        public CompilerException(String message) {
            super(message);
        }
    }
}
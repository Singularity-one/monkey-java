package com.monkey.compiler;

/**
 * Symbol 表示符號表中的一個符號
 * Chapter 5: Keeping Track of Names
 *
 * 符號記錄變量的名稱、作用域和索引
 */
public class Symbol {
    private final String name;       // 變量名稱
    private final SymbolScope scope; // 作用域
    private final int index;         // 在作用域中的索引

    public Symbol(String name, SymbolScope scope, int index) {
        this.name = name;
        this.scope = scope;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public SymbolScope getScope() {
        return scope;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("Symbol{name='%s', scope=%s, index=%d}",
                name, scope, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Symbol symbol = (Symbol) obj;
        return index == symbol.index &&
                name.equals(symbol.name) &&
                scope == symbol.scope;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + scope.hashCode();
        result = 31 * result + index;
        return result;
    }
}
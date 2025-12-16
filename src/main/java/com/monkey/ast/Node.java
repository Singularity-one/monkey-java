package com.monkey.ast;
import com.monkey.token.Token;

/**
 * Node 是 AST 中所有節點的基礎接口
 */
public interface Node {
    /**
     * 返回此節點關聯的 token 字面值
     * 主要用於調試和測試
     */
    String tokenLiteral();

    /**
     * 返回此節點的字串表示
     * 用於調試和查看 AST 結構
     */
    String string();

    Token getToken();
}

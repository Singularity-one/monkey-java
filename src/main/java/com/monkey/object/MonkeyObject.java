package com.monkey.object;
/**
 * MonkeyObject 是所有運行時物件的基礎接口
 */
public interface MonkeyObject {
    /**
     * 返回物件的類型
     */
    ObjectType type();

    /**
     * 返回物件的字串表示
     */
    String inspect();
}

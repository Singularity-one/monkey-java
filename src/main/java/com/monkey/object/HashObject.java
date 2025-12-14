package com.monkey.object;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HashObject 代表雜湊表
 */
public class HashObject implements MonkeyObject {
    private final Map<HashKey, HashPair> pairs;

    public HashObject(Map<HashKey, HashPair> pairs) {
        this.pairs = pairs;
    }

    public Map<HashKey, HashPair> getPairs() {
        return pairs;
    }

    @Override
    public ObjectType type() {
        return ObjectType.HASH;
    }

    @Override
    public String inspect() {
        String pairsStr = pairs.values().stream()
                .map(pair -> pair.key.inspect() + ": " + pair.value.inspect())
                .collect(Collectors.joining(", "));
        return "{" + pairsStr + "}";
    }

    @Override
    public HashKey hashKey() {
        return null;
    }

    /**
     * HashPair 儲存鍵值對
     */
    public static class HashPair {
        public final MonkeyObject key;
        public final MonkeyObject value;

        public HashPair(MonkeyObject key, MonkeyObject value) {
            this.key = key;
            this.value = value;
        }
    }
}

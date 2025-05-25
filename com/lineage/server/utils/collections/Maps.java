package com.lineage.server.utils.collections;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Maps {
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.utils.collections.Maps JD-Core Version: 0.6.2
 */
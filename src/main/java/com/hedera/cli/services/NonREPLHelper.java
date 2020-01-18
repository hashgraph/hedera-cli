package com.hedera.cli.services;

import java.util.HashMap;
import java.util.Map;

public class NonREPLHelper {
    public static Map<String, String> cache = new HashMap<>();

    public static Map<String, String> getCache() {
        return cache;
    }

    // Put data in global cache variable
    public static void putCache(String key, String value) {
        cache.put(key, value);
    }

    // by default, cli is in interactive mode (mode = true)
    // if user provides -X, cli executes in non-interactive mode (mode = false)
    public static boolean getInteractiveMode() {
        boolean mode = true;
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if ((k.equals("X") && v.equals("false")) || k.equals("S")) {
                mode = false;
            }
        }
        return mode;
    }
}
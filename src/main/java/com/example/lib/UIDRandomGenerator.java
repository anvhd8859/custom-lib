package com.example.lib;

import java.util.Random;

public class UIDRandomGenerator {
    private static final char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
    private static final SequenceGenerator generator = new SequenceGenerator();
    private static final Random random = new Random();

    public static String generateUID() {
        long id = generator.nextLong();
        StringBuilder sb = new StringBuilder();
        while (id != 0L) {
            int i = (int) (id % chars.length);
            sb.append(chars[i]);
            id /= chars.length;
        }
        return sb.toString();
    }
}

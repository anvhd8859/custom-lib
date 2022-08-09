package com.example.lib;

import java.util.Random;

public class UIDRandomGenerator {
    private static final char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
    private static final Random random = new Random();

    public static String generateUID() {
        long time = System.currentTimeMillis();
        time += (random.nextInt(300000) * 2 + 500000);
        StringBuilder sb = new StringBuilder();
        while (time != 0L) {
            int i = (int) (time % chars.length);
            sb.append(chars[i]);
            time /= chars.length;
        }
        if (sb.length() < 12) {
            while (sb.length() < 12) {
                sb.append(chars[random.nextInt(chars.length)]);
            }
        }
        return sb.toString();
    }
}

package com.schoolmanager.graduation_backend.util;

import java.nio.charset.StandardCharsets;

public final class VietnameseTextFixer {

    private VietnameseTextFixer() {
    }

    public static String fix(String value) {
        if (value == null || !looksMojibake(value)) {
            return value;
        }

        String decoded = new String(toOriginalUtf8Bytes(value), StandardCharsets.UTF_8);
        return badnessScore(decoded) < badnessScore(value) ? decoded : value;
    }

    private static boolean looksMojibake(String value) {
        return value.indexOf('Ã') >= 0
            || value.indexOf('Ä') >= 0
            || value.indexOf('Å') >= 0
            || value.indexOf('Æ') >= 0
            || value.contains("áº")
            || value.contains("á»")
            || value.contains("Â");
    }

    private static byte[] toOriginalUtf8Bytes(String value) {
        byte[] bytes = new byte[value.length()];
        for (int i = 0; i < value.length(); i++) {
            bytes[i] = switch (value.charAt(i)) {
                case '\u2018' -> (byte) 0x91;
                case '\u2019' -> (byte) 0x92;
                case '\u201C' -> (byte) 0x93;
                case '\u201D' -> (byte) 0x94;
                case '\u20AC' -> (byte) 0x80;
                case '\u201A' -> (byte) 0x82;
                case '\u0192' -> (byte) 0x83;
                case '\u201E' -> (byte) 0x84;
                case '\u2026' -> (byte) 0x85;
                case '\u2020' -> (byte) 0x86;
                case '\u2021' -> (byte) 0x87;
                case '\u02C6' -> (byte) 0x88;
                case '\u2030' -> (byte) 0x89;
                case '\u0160' -> (byte) 0x8A;
                case '\u2039' -> (byte) 0x8B;
                case '\u0152' -> (byte) 0x8C;
                case '\u017D' -> (byte) 0x8E;
                case '\u2022' -> (byte) 0x95;
                case '\u2013' -> (byte) 0x96;
                case '\u2014' -> (byte) 0x97;
                case '\u02DC' -> (byte) 0x98;
                case '\u2122' -> (byte) 0x99;
                case '\u0161' -> (byte) 0x9A;
                case '\u203A' -> (byte) 0x9B;
                case '\u0153' -> (byte) 0x9C;
                case '\u017E' -> (byte) 0x9E;
                case '\u0178' -> (byte) 0x9F;
                default -> (byte) value.charAt(i);
            };
        }
        return bytes;
    }

    private static long badnessScore(String value) {
        long score = value.chars().filter(ch -> ch == '\uFFFD').count() * 10;
        if (looksMojibake(value)) {
            score += 5;
        }
        return score;
    }
}

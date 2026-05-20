package com.schoolmanager.graduation_backend.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VietnameseTextFixerTest {

    @Test
    void fixesUtf8TextDecodedAsLatin1() {
        assertEquals("Sinh viên", VietnameseTextFixer.fix("Sinh viÃªn"));
        assertEquals("Đạt", VietnameseTextFixer.fix("Äáº¡t"));
        assertEquals("TOEIC 450 hoặc B1", VietnameseTextFixer.fix("TOEIC 450 hoáº·c B1"));
        assertEquals("Đủ điều kiện TN", VietnameseTextFixer.fix("Äá»§ Ä‘iá»u kiá»‡n TN"));
    }

    @Test
    void keepsValidVietnameseTextUnchanged() {
        assertEquals("Ngô Phương Nhi", VietnameseTextFixer.fix("Ngô Phương Nhi"));
        assertEquals("Đủ điều kiện TN", VietnameseTextFixer.fix("Đủ điều kiện TN"));
    }
}

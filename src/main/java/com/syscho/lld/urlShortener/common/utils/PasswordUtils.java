package com.syscho.lld.urlShortener.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String encode(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

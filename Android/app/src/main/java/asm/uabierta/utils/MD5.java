package asm.uabierta.utils;

import java.security.MessageDigest;
import java.util.ArrayList;

import asm.uabierta.models.Found;

/**
 * Created by Alex on 26/07/2016.
 */
public class MD5 {

    public static String createMd5(String pass) throws Exception {

        String original = pass;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

}
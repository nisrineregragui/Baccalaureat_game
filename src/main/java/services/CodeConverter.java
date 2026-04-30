package services;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CodeConverter {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();


    public static String ipToCode(String ip) {
        try {
            String[] parts = ip.split("\\.");
            long ipNum = 0;
            for (String part : parts) {
                ipNum = (ipNum << 8) + Integer.parseInt(part);
            }
            return toBase62(ipNum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String codeToIp(String code) {
        try {
            long ipNum = fromBase62(code);

            // Reconstruit l'IP string
            return String.format("%d.%d.%d.%d",
                    (ipNum >> 24) & 0xFF,
                    (ipNum >> 16) & 0xFF,
                    (ipNum >> 8) & 0xFF,
                    ipNum & 0xFF);
        } catch (Exception e) {
            return null;
        }
    }

    private static String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        if (value == 0)
            return String.valueOf(ALPHABET.charAt(0));

        while (value > 0) {
            sb.insert(0, ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.toString();
    }

    private static long fromBase62(String code) {
        long value = 0;
        for (char c : code.toCharArray()) {
            value = value * BASE + ALPHABET.indexOf(c);
        }
        return value;
    }
}

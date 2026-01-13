package services;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CodeConverter {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    /**
     * Convertit une IP (ex: "192.168.1.50") en un code court (ex: "3X7a9")
     * On suppose que c'est une IPv4.
     */
    public static String ipToCode(String ip) {
        try {
            // Convertit l'IP en long
            String[] parts = ip.split("\\.");
            long ipNum = 0;
            for (String part : parts) {
                ipNum = (ipNum << 8) + Integer.parseInt(part);
            }

            // Encode en Base62
            return toBase62(ipNum);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convertit un code (ex: "3X7a9") en IP (ex: "192.168.1.50")
     */
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

package util;

import entity.Patient;

import java.security.MessageDigest;


public class HashUtil {

    public static String generateHash(Patient patient) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            String data = patient.getId() +
                    patient.getName() +
                    patient.getDiagnosis();

            byte[] hashBytes = digest.digest(data.getBytes());

            return bytesToHex(hashBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);

        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}

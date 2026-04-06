package com.mp.capstone.project.util;

import com.mp.capstone.project.entity.Patient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashUtil {

    private HashUtil() {} // utility class — prevent instantiation

    public static String generateHash(Patient patient) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Delimited to prevent collision between different field splits
            // e.g. id="AB", name="C" vs id="A", name="BC" → now distinct
            // lastUpdated included so timestamp changes are detected
            String data = String.join("|",
                    patient.getId(),
                    patient.getName(),
                    patient.getDiagnosis(),
                    patient.getLastUpdated().toString()
            );

            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JCA spec — this cannot happen
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
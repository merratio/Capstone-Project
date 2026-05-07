package com.mp.capstone.project.util;

import com.mp.capstone.project.entity.MedicalRecord;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

public class HashUtil {

    private HashUtil() {} // utility class — prevent instantiation

    // Explicit formatter so lastUpdated always produces the same string
    // regardless of nanosecond precision differences between environments
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static String generateHash(MedicalRecord record) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Delimited to prevent collision between different field splits
            // e.g. id="AB", name="C" vs id="A", name="BC" → now distinct
            String data = String.join("|",
                    record.getId(),
                    record.getConditionName(),
                    record.getStatus(),
                    record.getDiagnosisDate().toString(),         // LocalDate.toString() is stable ISO-8601: "2026-05-07"
                    String.valueOf(record.getHereditary()),
                    record.getLastUpdated()
                            .truncatedTo(ChronoUnit.MILLIS)       // normalise to millis before formatting
                            .format(TIMESTAMP_FORMAT)             // explicit format — never varies
            );

            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JCA spec — this cannot happen
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
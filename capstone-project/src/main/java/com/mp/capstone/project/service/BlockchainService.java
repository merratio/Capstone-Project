package com.mp.capstone.project.service;

import com.mp.capstone.project.blockchain.FabricClient;
import com.mp.capstone.project.exception.BlockchainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);
    private final FabricClient fabricClient;

    public BlockchainService(FabricClient fabricClient) {
        this.fabricClient = fabricClient;
    }

    public void storeHash(String patientId, String hash) {
        try {
            logger.info("Storing hash on blockchain for patient: {}", patientId);
            fabricClient.submit("storeHash", patientId, hash);
            logger.info("Hash stored successfully for patient: {}", patientId);
        } catch (Exception e) {
            logger.error("Failed to store hash for patient: {}", patientId, e);
            throw new BlockchainException("Failed to store hash for patient: " + patientId, e);
        }
    }

    public void updateHash(String patientId, String hash) {
        try {
            logger.info("Updating hash on blockchain for patient: {}", patientId);
            fabricClient.submit("updateHash", patientId, hash);
            logger.info("Hash updated successfully for patient: {}", patientId);
        } catch (Exception e) {
            logger.error("Failed to update hash for patient: {}", patientId, e);
            throw new BlockchainException("Failed to update hash for patient: " + patientId, e);
        }
    }

    public String getHash(String patientId) {
        try {
            logger.info("Getting hash from blockchain for patient: {}", patientId);
            String hash = fabricClient.evaluate("getHash", patientId);
            logger.info("Hash retrieved successfully for patient: {}", patientId);
            return hash;
        } catch (Exception e) {
            logger.error("Failed to get hash for patient: {}", patientId, e);
            throw new BlockchainException("Failed to get hash for patient: " + patientId, e);
        }
    }

    public void deleteHash(String patientId) {
        try {
            logger.info("Deleting hash from blockchain for patient: {}", patientId);
            fabricClient.submit("deleteHash", patientId);
            logger.info("Hash deleted successfully for patient: {}", patientId);
        } catch (Exception e) {
            logger.error("Failed to delete hash for patient: {}", patientId, e);
            throw new BlockchainException("Failed to delete hash for patient: " + patientId, e);
        }
    }
}
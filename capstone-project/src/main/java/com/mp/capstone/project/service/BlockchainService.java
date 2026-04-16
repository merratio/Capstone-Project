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

    public void storeHash(String recordId, String hash) {
        try {
            logger.info("Storing hash on blockchain for record: {}", recordId);
            fabricClient.submit("storeHash", recordId, hash);
            logger.info("Hash stored successfully for record: {}", recordId);
        } catch (Exception e) {
            logger.error("Failed to store hash for record: {}", recordId, e);
            throw new BlockchainException("Failed to store hash for record: " + recordId, e);
        }
    }

    public void updateHash(String recordId, String hash) {
        try {
            logger.info("Updating hash on blockchain for record: {}", recordId);
            fabricClient.submit("updateHash", recordId, hash);
            logger.info("Hash updated successfully for record: {}", recordId);
        } catch (Exception e) {
            logger.error("Failed to update hash for record: {}", recordId, e);
            throw new BlockchainException("Failed to update hash for record: " + recordId, e);
        }
    }

    public String getHash(String recordId) {
        try {
            logger.info("Getting hash from blockchain for record: {}", recordId);
            String hash = fabricClient.evaluate("getHash", recordId);
            logger.info("Hash retrieved successfully for record: {}", recordId);
            return hash;
        } catch (Exception e) {
            logger.error("Failed to get hash for record: {}", recordId, e);
            throw new BlockchainException("Failed to get hash for record: " + recordId, e);
        }
    }

    public void deleteHash(String recordId) {
        try {
            logger.info("Deleting hash from blockchain for record: {}", recordId);
            fabricClient.submit("deleteHash", recordId);
            logger.info("Hash deleted successfully for record: {}", recordId);
        } catch (Exception e) {
            logger.error("Failed to delete hash for record: {}", recordId, e);
            throw new BlockchainException("Failed to delete hash for record: " + recordId, e);
        }
    }
}
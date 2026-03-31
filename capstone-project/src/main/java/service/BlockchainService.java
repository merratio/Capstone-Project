package service;

import blockchain.FabricClient;
import org.springframework.stereotype.Service;

@Service
public class BlockchainService {

    private final FabricClient fabricClient;

    public BlockchainService(FabricClient fabricClient) {
        this.fabricClient = fabricClient;
    }

    public void storeHash(String id, String hash) {
        fabricClient.submit("storeHash", id, hash);
    }

    public void updateHash(String id, String hash) {
        fabricClient.submit("updateHash", id, hash);
    }

    public String getHash(String id) {
        return fabricClient.evaluate("getHash", id);
    }
}
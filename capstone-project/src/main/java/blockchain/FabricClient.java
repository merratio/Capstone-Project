package blockchain;

import exception.BlockchainException;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Component;

@Component
public class FabricClient {

    private final Contract contract;

    public FabricClient(Contract contract) {
        this.contract = contract;
    }

    public byte[] submit(String function, String... args) {
        try {
            return contract.submitTransaction(function, args);
        } catch (Exception e) {
            throw new BlockchainException(
                    "Failed to submit transaction for function: " + function, e);
        }
    }

    public String evaluate(String function, String... args) {
        try {
            return new String(contract.evaluateTransaction(function, args));
        } catch (Exception e) {
            throw new BlockchainException(
                    "Failed to evaluate transaction for function: " + function, e);
        }
    }
}
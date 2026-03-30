package blockchain;

import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Component;

@Component
public class FabricClient {

    private final Contract contract;

    public FabricClient(Contract contract) {
        this.contract = contract;
    }

    public void submit(String function, String... args) {
        try {
            contract.submitTransaction(function, args);
        } catch (Exception e) {
            throw new RuntimeException("Blockchain error", e);
        }
    }

    public String evaluate(String function, String... args) {
        try {
            return new String(contract.evaluateTransaction(function, args));
        } catch (Exception e) {
            throw new RuntimeException("Blockchain error", e);
        }
    }
}
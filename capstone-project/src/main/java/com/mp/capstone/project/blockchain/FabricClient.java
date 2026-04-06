package com.mp.capstone.project.blockchain;

import com.mp.capstone.project.exception.BlockchainException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.SubmitException;import org.springframework.stereotype.Component;

@Component
public class FabricClient {

    private final Contract contract;

    public FabricClient(Contract contract) {
        this.contract = contract;
    }

    public void submit(String function, String... args) {
        try {
            contract.submitTransaction(function, args);
        } catch (EndorseException | SubmitException e) {
            throw new BlockchainException("Failed to submit transaction for function: " + function, e);
        } catch (Exception e) {
            throw new BlockchainException("Unexpected error submitting function: " + function, e);
        }
    }

    public String evaluate(String function, String... args) {
        try {
            return new String(contract.evaluateTransaction(function, args));
        } catch (GatewayException e) {
            throw new BlockchainException("Failed to evaluate transaction for function: " + function, e);
        }
    }
}
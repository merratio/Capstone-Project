package blockchain;

import org.hyperledger.fabric.gateway.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FabricGatewayConfig {

    @Bean
    public Gateway gateway() throws Exception {

        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        Path networkConfigPath = Paths.get("connection.json");

        return Gateway.createBuilder()
                .identity(wallet, "appUser")
                .networkConfig(networkConfigPath)
                .discovery(true)
                .connect();
    }

    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork("mychannel");
    }

    @Bean
    public Contract contract(Network network) {
        return network.getContract("integritycc");
    }
}

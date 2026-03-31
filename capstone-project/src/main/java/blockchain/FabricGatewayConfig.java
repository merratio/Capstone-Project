package blockchain;

import org.hyperledger.fabric.gateway.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class FabricGatewayConfig {

    @Value("${fabric.wallet-path}")
    private String walletPath;

    @Value("${fabric.user}")
    private String fabricUser;

    @Value("${fabric.network-config-path}")
    private String networkConfigPath;

    @Value("${fabric.channel}")
    private String channel;

    @Value("${fabric.chaincode}")
    private String chaincode;

    @Bean(destroyMethod = "close")
    public Gateway gateway() throws Exception {
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));

        return Gateway.createBuilder()
                .identity(wallet, fabricUser)
                .networkConfig(Paths.get(networkConfigPath))
                .discovery(true)
                .connect();
    }

    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork(channel);
    }

    @Bean
    public Contract contract(Network network) {
        return network.getContract(chaincode);
    }
}
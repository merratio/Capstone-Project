package com.mp.capstone.project.blockchain;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Hash;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Configuration
public class FabricGatewayConfig {

    @Value("${fabric.mspid}")
    private String mspId;

    @Value("${fabric.channel}")
    private String channel;

    @Value("${fabric.chaincode}")
    private String chaincode;

    @Value("${fabric.peer.endpoint}")
    private String peerEndpoint;

    @Value("${fabric.peer.override-auth}")
    private String overrideAuth;

    @Value("${fabric.crypto.cert-dir-path}")
    private String certDirPath;

    @Value("${fabric.crypto.key-dir-path}")
    private String keyDirPath;

    @Value("${fabric.crypto.tls-cert-path}")
    private String tlsCertPath;

    @Lazy
    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel grpcChannel() throws IOException {
        var credentials = TlsChannelCredentials.newBuilder()
                .trustManager(Paths.get(tlsCertPath).toFile())
                .build();
        return Grpc.newChannelBuilder(peerEndpoint, credentials)
                .overrideAuthority(overrideAuth)
                .build();
    }

    @Lazy
    @Bean(destroyMethod = "close")
    public Gateway gateway(ManagedChannel grpcChannel) throws Exception {
        return Gateway.newInstance()
                .identity(newIdentity())
                .signer(newSigner())
                .hash(Hash.SHA256)
                .connection(grpcChannel)
                .evaluateOptions(options -> options.withDeadlineAfter(30, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(60, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(60, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(2, TimeUnit.MINUTES))
                .connect();
    }
    
    @Lazy
    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork(channel);
    }

    @Lazy
    @Bean
    public Contract contract(Network network) {
        return network.getContract(chaincode);
    }

    private Identity newIdentity() throws IOException, CertificateException {
        try (var certReader = Files.newBufferedReader(getFirstFilePath(Paths.get(certDirPath)))) {
            var certificate = Identities.readX509Certificate(certReader);
            return new X509Identity(mspId, certificate);
        }
    }

    private Signer newSigner() throws IOException, InvalidKeyException {
        try (var keyReader = Files.newBufferedReader(getFirstFilePath(Paths.get(keyDirPath)))) {
            var privateKey = Identities.readPrivateKey(keyReader);
            return Signers.newPrivateKeySigner(privateKey);
        }
    }

    private Path getFirstFilePath(Path dirPath) throws IOException {
        try (Stream<Path> files = Files.list(dirPath)) {
            return files.findFirst().orElseThrow();
        }
    }
}
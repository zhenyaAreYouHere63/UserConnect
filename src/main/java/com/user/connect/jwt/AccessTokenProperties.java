package com.user.connect.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Getter
@Setter
@Component
public class AccessTokenProperties {

    @Value("${auth.jwt.accessToken.expirationTimeInMinutes}")
    public Long expirationTimeInMinutes;

    private KeyPair accessTokenPair;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        accessTokenPair = generateKeyPair();
    }

    public String secretKey() {
        return encodePrivateKeyToBase64(accessTokenPair.getPrivate());
    }

    public String publicKey() {
        return encodePublicKeyToBase64(accessTokenPair.getPublic());
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private String encodePublicKeyToBase64(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    private String encodePrivateKeyToBase64(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }
}

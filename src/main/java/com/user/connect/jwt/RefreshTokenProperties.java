package com.user.connect.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Getter
@Setter
@Component
public class RefreshTokenProperties {

    @Value("${auth.jwt.refreshToken.expirationTimeInMinutes}")
    public Long expirationTimeInMinutes;

    private KeyPair refreshTokenPair;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        refreshTokenPair = generateKeyPair();
    }

    public String secretKey() {
        return encodePrivateKeyToBase64(refreshTokenPair.getPrivate());
    }

    public String publicKey() {
        return encodePublicKeyToBase64(refreshTokenPair.getPublic());
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

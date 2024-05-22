package com.user.connect.service.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    public static PrivateKey convertBase64PrivateKeyToJavaPrivateKey(
            String base64PrivateKey,
            KeyFactory keyFactory
    ) throws InvalidKeySpecException {
        try {
            byte[] decodedBase64PrivateKey = Base64.getDecoder().decode(base64PrivateKey);
            var pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decodedBase64PrivateKey);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new InvalidKeySpecException("Failed to convert base64 private key to java private key", e);
        }
    }

    public static PublicKey convertBase64PublicKeyToJavaPublicKey(
            String base64PublicKey,
            KeyFactory keyFactory
    ) throws InvalidKeySpecException {
        byte[] decodedBase64PublicKey = Base64.getDecoder().decode(base64PublicKey);
        var x509EncodedKeySpec = new X509EncodedKeySpec(decodedBase64PublicKey);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }
}

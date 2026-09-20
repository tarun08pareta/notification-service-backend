package com.notificationengine.apiToken.service;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class ApiTokenEncryptor {
    // Aap yaha apni marzi ka koi bhi secret password rakh sakte hain
    private static final String SECRET = "MySuperSecretNotificationEngineKey2026";
    private static final String ALGORITHM = "AES";

    private SecretKeySpec getKey() {
        try {
            // Yeh code kisi bhi string ko exact 32 bytes (AES-256 ke liye) mein badal dega
            byte[] key = SECRET.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting token", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting token", e);
        }
    }
}
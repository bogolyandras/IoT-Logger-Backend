package com.bogolyandras.iotlogger.utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptographyUtility {

    private static final String HS256Name = "HmacSHA256";

    public static byte[] signWithHS256(byte[] item, byte[] key) {
        try {
            Mac hmacSHA256 = Mac.getInstance(HS256Name);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, HS256Name);
            hmacSHA256.init(secretKeySpec);
            return hmacSHA256.doFinal(item);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}

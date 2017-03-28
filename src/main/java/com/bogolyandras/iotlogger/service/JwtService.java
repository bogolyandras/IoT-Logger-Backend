package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.exception.JwtException;
import com.bogolyandras.iotlogger.utility.CryptographyUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class JwtService {

    private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

    public JwtService(@Value("${web.signingKey:#{null}}") String signingKey) {
        if (signingKey == null) {
            byte[] randomBytes = new byte[30];
            new Random().nextBytes(randomBytes);
            this.signingKey = randomBytes;
        } else {
            this.signingKey = signingKey.getBytes();
        }
    }
    private byte[] signingKey;

    public String issueToken(String subject) {
        long issuedAt = System.currentTimeMillis() / 1000L;
        long expires = issuedAt + 3600 * 24 * 30;
        Map<String, String> header = new HashMap<String, String>() {{
            put("alg", "HS256");
            put("typ", "JWT");
        }};
        Map<String, Object> payload = new HashMap<String, Object>() {{
            put("sub", subject);
            put("iat", issuedAt);
            put("exp", expires);
        }};
        String headerAsJson;
        String payloadAsJson;
        try {
            headerAsJson = jsonObjectMapper.writeValueAsString(header);
            payloadAsJson = jsonObjectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String toBeSigned =
                new String(Base64.getUrlEncoder().encode( headerAsJson.getBytes() ))
                        +
                        "."
                        +
                        new String(Base64.getUrlEncoder().encode(  payloadAsJson.getBytes() ));
        byte[] signature = CryptographyUtility.signWithHS256(toBeSigned.getBytes(StandardCharsets.UTF_8), signingKey);
        return toBeSigned + "." + new String(Base64.getUrlEncoder().encode(signature));
    }
    public String verifyToken(String token) throws JwtException {

        String[] tokenParts = token.split("\\.");
        if (tokenParts.length != 3) {
            throw new JwtException("Not a valid JWT token: it has " + tokenParts.length + " parts instead of 3");
        }
        byte[] signed = (tokenParts[0] + "." + tokenParts[1]).getBytes();
        byte[] providedSignature = Base64.getUrlDecoder().decode(tokenParts[2]);
        byte[] correctSignature = CryptographyUtility.signWithHS256(signed, signingKey);
        if (!new String(providedSignature).equals(new String(correctSignature))) {
            throw new JwtException("Invalid Jwt signature provided!");
        }
        HashMap<String, Object> payloadValues;
        try {
            payloadValues = jsonObjectMapper.readValue(Base64.getUrlDecoder().decode(tokenParts[1]), new TypeReference<HashMap<String, Object>>(){});
        } catch (IOException e) {
            throw new JwtException("Unable to parse JSON", e);
        }
        Object rawExpiringDate = payloadValues.get("exp");
        if (rawExpiringDate == null) {
            throw new JwtException("Expiring date cannot be found");
        }
        Long expires;
        if (rawExpiringDate instanceof Integer) {
            expires = ((Integer) rawExpiringDate).longValue();
        } else if (rawExpiringDate instanceof Long) {
            expires = (Long) rawExpiringDate;
        } else {
            throw new JwtException("Cannot determine expiration date of token");
        }
        if (expires < System.currentTimeMillis() / 1000L) {
            throw new JwtException("Token has been expired!");
        }
        if (payloadValues.get("sub") == null) {
            throw new JwtException("Userid cannot be found");
        }

        return (String) payloadValues.get("sub");
    }

}

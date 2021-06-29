package com.lulobank.clients.services.utils;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class LuloUserTokenGenerator {
  private final JWEHeader header;
  private final RSAEncrypter encrypter;

  public LuloUserTokenGenerator(String publicKeyStr)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    publicKeyStr =
        publicKeyStr
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "");
    KeyFactory kf = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpecX509 =
        new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));
    RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

    // Request JWT encrypted with RSA-OAEP-256 and 128-bit AES/GCM
    header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A256GCM);
    // Create an encrypter with the specified public RSA key
    encrypter = new RSAEncrypter(publicKey);
  }

  public String getUserToken(String sub) throws JOSEException {
    LocalDateTime ldtNow =DatesUtil.getLocalDateGMT5();
    JWTClaimsSet jwtClaims =
        new JWTClaimsSet.Builder()
            .issuer("https://lulobank.com.co")
            .subject(sub)
            .expirationTime(
                new Date(
                    ldtNow.toInstant(ZoneOffset.ofHours(-5)).toEpochMilli()
                        + 1000 * 60 * 10)) // expires in 10 minutes
            .notBeforeTime(new Date(ldtNow.toInstant(ZoneOffset.ofHours(-5)).toEpochMilli()))
            .issueTime(new Date(ldtNow.toInstant(ZoneOffset.ofHours(-5)).toEpochMilli()))
            .jwtID(UUID.randomUUID().toString())
            .build();

    // Create the encrypted JWT object
    EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

    // Do the actual encryption
    jwt.encrypt(encrypter);
    // Serialise to JWT compact form
    return jwt.serialize();
  }
}

package com.lulobank.clients.starter.utils;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class BearerTokenRequestPostProcessor implements RequestPostProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(BearerTokenRequestPostProcessor.class);
  private static final String SUBJECT_ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  private RSAPublicKey publicKey;

  @Override
  public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {

    try {
      String publicKeyContent =
          new String(
              Files.readAllBytes(
                  Paths.get(ClassLoader.getSystemResource("public_key.pem").toURI())));
      publicKeyContent =
          publicKeyContent
              .replaceAll("\\n", "")
              .replaceAll("\\r", "")
              .replace("-----BEGIN PUBLIC KEY-----", "")
              .replace("-----END PUBLIC KEY-----", "");

      KeyFactory kf = KeyFactory.getInstance("RSA");

      X509EncodedKeySpec keySpecX509 =
          new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
      publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

      // Compose the JWT claims set
      Date now = new Date();

      JWTClaimsSet jwtClaims =
          new JWTClaimsSet.Builder()
              .issuer("https://openid.net")
              .subject(SUBJECT_ID_CLIENT)
              .audience(Arrays.asList("https://app-one.com", "https://app-two.com"))
              .expirationTime(new Date(now.getTime() + 1000 * 60 * 10)) // expires in 10 minutes
              .notBeforeTime(now)
              .issueTime(now)
              .jwtID(UUID.randomUUID().toString())
              .build();

      // Request JWT encrypted with RSA-OAEP-256 and 128-bit AES/GCM
      JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A256GCM);

      // Create the encrypted JWT object
      EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

      // Create an encrypter with the specified public RSA key
      RSAEncrypter encrypter = new RSAEncrypter(publicKey);

      // Do the actual encryption
      jwt.encrypt(encrypter);

      // Serialise to JWT compact form
      String jwtString = jwt.serialize();

      request.addHeader("Authorization", "Bearer " + jwtString);

    } catch (IOException
        | URISyntaxException
        | NoSuchAlgorithmException
        | InvalidKeySpecException
        | JOSEException e) {
      LOGGER.error("Se presento un error generando el token.", e);
    }

    return request;
  }
}

package com.lulobank.clients.starter.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lulobank.clients.services.utils.LogMessages;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConfig.class);

  @Value("${cloud.google.firebase.database-url}")
  private String firebaseDBUrl;

  @Bean
  public DatabaseReference firebaseDatabase() {
    FirebaseOptions options;
    try {
      options =
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.getApplicationDefault())
              .setDatabaseUrl(firebaseDBUrl)
              .build();
      FirebaseApp.initializeApp(options);

    } catch (IOException e) {
      LOGGER.error(LogMessages.ERROR_FIREBASE_CONFIG.getMessage(), e.getMessage(), e);
    }

    return FirebaseDatabase.getInstance().getReference();
  }
}

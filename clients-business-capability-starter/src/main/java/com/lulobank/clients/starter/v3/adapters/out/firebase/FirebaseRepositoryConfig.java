package com.lulobank.clients.starter.v3.adapters.out.firebase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.firebase.database.DatabaseReference;

@Configuration
public class FirebaseRepositoryConfig {
	
	@Bean
	public UserStateFirebaseRepository getUserStateFirebaseRepository(DatabaseReference databaseReference) {
		return new UserStateFirebaseRepository(databaseReference);
	}

}

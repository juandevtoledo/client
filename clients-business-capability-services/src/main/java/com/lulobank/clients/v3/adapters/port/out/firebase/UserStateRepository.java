package com.lulobank.clients.v3.adapters.port.out.firebase;

public interface UserStateRepository {
	
	void notifyCreated(String idClient, String productSelected);
}

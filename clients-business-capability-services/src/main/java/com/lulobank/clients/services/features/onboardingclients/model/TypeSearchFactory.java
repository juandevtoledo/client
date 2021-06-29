package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.clients.sdk.operations.util.SearchType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeSearchFactory {

  static Map<String, TypeSearch> typeSearchMap = new HashMap<>();

  private static final String TYPE_SEARCH_BY_PHONE = SearchType.PHONE_NUMBER.name();
  private static final String TYPE_SEARCH_BY_EMAIL = SearchType.EMAIL.name();
  private static final String TYPE_SEARCH_BY_ID_CLIENT = SearchType.ID_CLIENT.name();

  static {
    typeSearchMap.put(TYPE_SEARCH_BY_PHONE, new ClientInfoByPhone());
    typeSearchMap.put(TYPE_SEARCH_BY_EMAIL, new ClientInfoByEmail());
    typeSearchMap.put(TYPE_SEARCH_BY_ID_CLIENT, new ClientInfoByIdClient());
  }

  public static Optional<TypeSearch> getTypeSearch(String typeSearch) {
    return Optional.ofNullable(typeSearchMap.get(typeSearch));
  }
}

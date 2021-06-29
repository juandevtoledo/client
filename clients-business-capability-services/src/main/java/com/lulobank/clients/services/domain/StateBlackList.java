package com.lulobank.clients.services.domain;

public enum StateBlackList {
  WAITING_FOR_VERIFICATION,
  BLACKLISTED,
  NON_BLACKLISTED,
  STARTED,
  FAILED,
  BIOMETRY_FAILED,
  WHITELISTED,
  BLACKLIST_COMPLIANCE
}

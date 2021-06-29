package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentCreateClientRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private String key;
  private String link;
}

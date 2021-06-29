package com.lulobank.clients.services.usecase.command;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SaveDigitalEvidence extends AbstractCommandFeatures {

  private String idClient ;
  private DigitalEvidenceTypes digitalEvidenceTypes;

}

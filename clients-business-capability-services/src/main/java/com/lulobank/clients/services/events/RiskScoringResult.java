package com.lulobank.clients.services.events;

import com.lulobank.core.Command;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiskScoringResult extends AbstractRetryFeature implements Command {
  private String status;
  private List<Results> results;
}

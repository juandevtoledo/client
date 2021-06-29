package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import io.vavr.control.Option;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedRiskAnalysisV3 {
    private String status;
    private List<RiskOfferV3> results;
    
    public List<RiskOfferV3> getResults() {
    	Option.of(results)
			.onEmpty(() -> results = new ArrayList<>());
    	return results;
    }
}

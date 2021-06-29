package com.lulobank.clients.services.features.riskscoreresponse;

import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.outboundadapters.model.CreditRiskAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreditRiskAnalysisMapper {
  CreditRiskAnalysisMapper INSTANCE = Mappers.getMapper(CreditRiskAnalysisMapper.class);

  CreditRiskAnalysis riskScoringResultToCreditRiskAnalysis(RiskScoringResult riskScoringResult);
}

package com.lulobank.clients.starter.v3.adapters.in.sqs.config;

import co.com.lulobank.tracing.sqs.EventMessage;
import com.lulobank.clients.services.application.usecase.loanautomaticpayment.NotifyAutomaticPaymentUseCase;
import com.lulobank.clients.starter.v3.adapters.config.NotifyAutomaticPaymentUseCaseConfig;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.ActivateBlacklistedClientHandler;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.ActivatePepClientHandler;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.LoanAutomaticPaymentHandler;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.CreatePreApprovedOfferHandler;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.CreateProductOfferHandler;
import com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient.ActivateBlacklistedClientPort;
import com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient.ActivatePepClientPort;
import com.lulobank.clients.v3.usecase.productoffers.CreateClientProductOfferUseCase;
import com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer.CreatePreApprovedOfferPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(NotifyAutomaticPaymentUseCaseConfig.class)
public class SqsRegistryHandlerConfig {

    @EventMessage(name = "LoanAutomaticPayment")
    public LoanAutomaticPaymentHandler loanAutomaticPaymentHandler(NotifyAutomaticPaymentUseCase notifyAutomaticPaymentUseCase) {
        return new LoanAutomaticPaymentHandler(notifyAutomaticPaymentUseCase);
    }

    @EventMessage(name = "ActivateBlacklistedClient")
    public ActivateBlacklistedClientHandler activateBlacklistedClientHandler(ActivateBlacklistedClientPort activateBlacklistedClientPort){
        return new ActivateBlacklistedClientHandler(activateBlacklistedClientPort);
    }

    @EventMessage(name = "ActivateSelfCertifiedPEPClient")
    public ActivatePepClientHandler activateSelfCertifiedPEPClientHandler(ActivatePepClientPort activatePEPClientPort){
        return new ActivatePepClientHandler(activatePEPClientPort);
    }

    @EventMessage(name = "CreatePreApprovedOfferMessage")
    public CreatePreApprovedOfferHandler getCreatePreApprovedOfferHandler(CreatePreApprovedOfferPort createPreApprovedOfferPort) {
        return new CreatePreApprovedOfferHandler(createPreApprovedOfferPort);
    }
    
    @EventMessage(name = "CreateProductOfferMessage")
    public CreateProductOfferHandler getCreateProductOfferHandler(CreateClientProductOfferUseCase createClientProductOfferUseCase) {
        return new CreateProductOfferHandler(createClientProductOfferUseCase);
    }
}

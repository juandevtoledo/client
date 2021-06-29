package com.lulobank.clients.starter.config;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.sdk.operations.dto.GetClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.onboarding.changeproduct.ChangeOnBoardingSelectedProductToSavingsForClient;
import com.lulobank.clients.sdk.operations.dto.resetidentitybiometric.ClientToReset;
import com.lulobank.clients.services.actions.MessageToSQSEconomicInformation;
import com.lulobank.clients.services.actions.MessageToSQSIdentityInformation;
import com.lulobank.clients.services.actions.MessageToSQSUpdateIdBiometricIdentity;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.checkidentitybiometric.CheckIdentityBiometricHandler;
import com.lulobank.clients.services.features.clientverificationresult.BlacklistedProcessService;
import com.lulobank.clients.services.features.clientverificationresult.ClientVerificationResultHandler;
import com.lulobank.clients.services.features.clientverificationresult.NonBlacklistedProcessService;
import com.lulobank.clients.services.features.identitybiometric.UpdateIdTransactionBiometricHandler;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.features.identitybiometric.validators.UpdateIdTransactionBiometricValidator;
import com.lulobank.clients.services.features.infoclient.GetClientInfoByIdCardHandler;
import com.lulobank.clients.services.features.infoclient.validators.GetClientInfoByIdCardValidator;
import com.lulobank.clients.services.features.loanrequested.GenerateOfferAction;
import com.lulobank.clients.services.features.loanrequested.LoanRequestedHandler;
import com.lulobank.clients.services.features.loanrequested.LoanRequestedValidator;
import com.lulobank.clients.services.features.onboardingchangeproduct.ChangeOnBoardingSelectedProductToSavingsForClientHandler;
import com.lulobank.clients.services.features.onboardingchangeproduct.validators.ChangeOnBoardingProductToSavingsValidator;
import com.lulobank.clients.services.features.onboardingclients.ClientEconomicInformationHandler;
import com.lulobank.clients.services.features.onboardingclients.UpdateCreditAcceptHandler;
import com.lulobank.clients.services.features.onboardingclients.validators.EconomicInformationValidator;
import com.lulobank.clients.services.features.productsloanrequested.ProductsLoanRequestedHandler;
import com.lulobank.clients.services.features.productsloanrequested.ProductsLoanRequestedValidator;
import com.lulobank.clients.services.features.productsloanrequested.ProductsLoanRequestedWithClient;
import com.lulobank.clients.services.features.profile.UpdateClientAddressEventUseCase;
import com.lulobank.clients.services.features.profile.UpdateClientAddressService;
import com.lulobank.clients.services.features.profile.UpdateClientAddressUseCase;
import com.lulobank.clients.services.features.profilev2.UpdateClientEmailHandler;
import com.lulobank.clients.services.features.resetidentitybiometric.ResetBiometricIdentityHandler;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultEventFactory;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromHomeEventHandler;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultFromOnbordingEventHandler;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.services.ports.out.corebanking.ClientInfoCoreBankingPort;
import com.lulobank.clients.starter.v3.adapters.out.approvedriskengine.ApprovedRiskEngineNotificationConfig;
import com.lulobank.clients.starter.v3.adapters.out.credits.config.CreditsServiceAdapterConfig;
import com.lulobank.clients.v3.adapters.port.out.credits.CreditsService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.Validator;
import flexibility.client.sdk.FlexibilitySdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({ApprovedRiskEngineNotificationConfig.class, CreditsServiceAdapterConfig.class})
public class HandlerConfiguration {

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private ClientsV3Repository clientsV3Repository;

    @Value("${cloud.aws.sqs.client-events}")
    private String sqsClientEventsEndPoint;

    @Value("${cloud.aws.sqs.client-alerts-events}")
    private String sqsNotificationEndPoint;

    @Value("${cloud.aws.sqs.riskengine-events}")
    private String sqsRiskEngineEndPoint;

    @Value("${cloud.aws.sqs.customer-events}")
    private String sqsCustomerEndPoint;

    @Autowired
    private ClientsOutboundAdapter clientsOutboundAdapter;


    @Autowired
    @Qualifier("adotechResponseRetriesOption")
    private RetriesOption adotechResponseRetriesOption;

    @Autowired
    @Qualifier("mobileResponseRetriesOption")
    private RetriesOption mobileResponseRetriesOption;

    @Bean
    @Qualifier("loanRequestedDecoratorHandler")
    public ValidatorDecoratorHandler loanRequestedDecoratorHandler() {
        List<GenerateOfferAction> actions = new ArrayList<>();
        actions.add(new GenerateOfferAction(clientsOutboundAdapter));
        List<Validator<ClientLoanRequested>> validators = new ArrayList<>();
        validators.add(new LoanRequestedValidator());
        return new ValidatorDecoratorHandler(new PostActionsDecoratorHandler(
                new LoanRequestedHandler(clientsV3Repository), actions), validators);
    }

    @Bean
    @Qualifier("changeOnBoardingSelectedProductToSavingsForClientHandler")
    public ValidatorDecoratorHandler changeOnBoardingSelectedProductToSavingsForClientHandler() {
        List<Validator<ChangeOnBoardingSelectedProductToSavingsForClient>> validators =
                new ArrayList<>();
        validators.add(new ChangeOnBoardingProductToSavingsValidator());
        return new ValidatorDecoratorHandler<>(
                new ChangeOnBoardingSelectedProductToSavingsForClientHandler(clientsOutboundAdapter),
                validators);
    }

    @Bean
    @Qualifier("economicInformationDecoratorHandler")
    public ValidatorDecoratorHandler economicInformationDecoratorHandler(final ParameterService parameterService) {
        List<Validator<ClientEconomicInformation>> validators = new ArrayList<>();
        List<Action<Response, ClientEconomicInformation>> actions = new ArrayList<>();
        validators.add(new EconomicInformationValidator());

        actions.add(new MessageToSQSEconomicInformation(queueMessagingTemplate, sqsRiskEngineEndPoint));
        actions.add(new MessageToSQSIdentityInformation(queueMessagingTemplate, sqsRiskEngineEndPoint));

        return new ValidatorDecoratorHandler<>(new PostActionsDecoratorHandler<>(
                new ClientEconomicInformationHandler(clientsRepository, parameterService), actions), validators);
    }

    @Bean
    @Qualifier("updateIdTransactionBiometricHandler")
    public ValidatorDecoratorHandler updateIdTransactionBiometricHandler() {
        List<Action<Response, UpdateIdTransactionBiometric>> actions = new ArrayList<>();
        actions.add(
                new MessageToSQSUpdateIdBiometricIdentity(
                        queueMessagingTemplate, sqsClientEventsEndPoint, adotechResponseRetriesOption));
        List<Validator<UpdateIdTransactionBiometric>> validators = new ArrayList<>();
        validators.add(new UpdateIdTransactionBiometricValidator());
        return new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(
                        new UpdateIdTransactionBiometricHandler(clientsOutboundAdapter), actions),
                validators);
    }

    @Bean
    @Qualifier("resetBiometricIdentityHandler")
    public ValidatorDecoratorHandler resetBiometricIdentityHandler() {
        List<Validator<ClientToReset>> validators = new ArrayList<>();
        return new ValidatorDecoratorHandler<>(
                new ResetBiometricIdentityHandler(clientsOutboundAdapter), validators);
    }

    @Bean
    @Qualifier("productsLoanRequestedHandler")
    public ValidatorDecoratorHandler productsLoanRequestedHandler(CreditsService creditsService) {
        List<Validator<ProductsLoanRequestedWithClient>> validators = new ArrayList<>();
        validators.add(new ProductsLoanRequestedValidator(clientsRepository));
        return new ValidatorDecoratorHandler<>(
                new ProductsLoanRequestedHandler(clientsOutboundAdapter, creditsService), validators);
    }

    @Bean
    public ClientVerificationResultHandler clientVerificationResultHandler(NonBlacklistedProcessService nonBlacklistedProcessService,
                                                                           BlacklistedProcessService blacklistedProcessService,
                                                                           ReportingMessagingPort reportingMessagingPort,
                                                                           DigitalEvidenceService digitalEvidenceService) {
    	
        return new ClientVerificationResultHandler(clientsOutboundAdapter, mobileResponseRetriesOption,
                clientsV3Repository, nonBlacklistedProcessService, blacklistedProcessService, reportingMessagingPort, digitalEvidenceService);
    }

    @Bean
    public RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler() {
        return new RiskScoringResultFromHomeEventHandler(clientsOutboundAdapter);
    }

    @Bean
    public RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler() {
        return new RiskScoringResultFromOnbordingEventHandler(clientsOutboundAdapter);
    }

    @Bean
    public UpdateCreditAcceptHandler updateCreditAcceptHandler() {
        return new UpdateCreditAcceptHandler(clientsRepository);
    }

    @Bean
    public CheckIdentityBiometricHandler checkIdentityBiometricHandler() {
        return new CheckIdentityBiometricHandler(
                clientsOutboundAdapter, adotechResponseRetriesOption);
    }

    @Bean
    @Qualifier("getClientInfoByIdCardHandler")
    public ValidatorDecoratorHandler getClientInfoByIdCardHandler() {
        List<Validator<GetClientInformationByIdCard>> validators = new ArrayList<>();
        validators.add(new GetClientInfoByIdCardValidator());
        return new ValidatorDecoratorHandler<>(
                new GetClientInfoByIdCardHandler(clientsOutboundAdapter), validators);
    }

    @Bean
    public RiskScoringResultEventFactory riskScoringResultEventFactory(
            RiskScoringResultFromHomeEventHandler riskScoringResultFromHomeEventHandler,
            RiskScoringResultFromOnbordingEventHandler riskScoringResultFromOnbordingEventHandler) {
        return new RiskScoringResultEventFactory(
                clientsRepository,
                riskScoringResultFromHomeEventHandler,
                riskScoringResultFromOnbordingEventHandler);
    }

    @Bean
    public UpdateClientAddressUseCase updateClientInformationUseCase(UpdateClientAddressService updateClientAddressService) {
        return new UpdateClientAddressUseCase(updateClientAddressService);
    }

    @Bean
    public UpdateClientAddressEventUseCase updateClientAddressEventUseCase(UpdateClientAddressService updateClientAddressService,
                                                                           ClientsV3Repository clientsV3Repository) {
        return new UpdateClientAddressEventUseCase(updateClientAddressService, clientsV3Repository);
    }

    @Bean
    public UpdateClientAddressService updateClientAddressService(ClientsV3Repository clientsV3Repository,
                                                                 MessageService messageService,
                                                                 ClientInfoCoreBankingPort clientInfoCoreBankingPort) {
        return new UpdateClientAddressService(clientsV3Repository, messageService, clientInfoCoreBankingPort);
    }

    @Bean
    public UpdateClientEmailHandler updateClientEmailHandler(
            ClientsRepository clientsRepository,
            IdentityProviderService identityProviderService,
            QueueMessagingTemplate queueMessagingTemplate,
            MessageService messageQueuesService,
            FlexibilitySdk flexibilitySdk
    ) {
        return new UpdateClientEmailHandler(clientsRepository, identityProviderService, queueMessagingTemplate, sqsNotificationEndPoint, sqsCustomerEndPoint, messageQueuesService, flexibilitySdk);
    }
}

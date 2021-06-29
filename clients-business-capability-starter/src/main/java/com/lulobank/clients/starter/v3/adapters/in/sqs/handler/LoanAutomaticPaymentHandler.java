package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.services.application.usecase.loanautomaticpayment.NotifyAutomaticPaymentUseCase;
import com.lulobank.clients.services.domain.findclientbyidbsc.NotifyAutomaticPaymentRequest;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.LoanAutomaticPayment;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class LoanAutomaticPaymentHandler implements EventHandler<LoanAutomaticPayment> {

    private final NotifyAutomaticPaymentUseCase notifyAutomaticPaymentUseCase;

    @Override
    public Try<Void> execute(LoanAutomaticPayment event) {

        return notifyAutomaticPaymentUseCase.execute(buildRequest(event))
                .onSuccess(success -> log.info("LoanAutomaticPayment Event was successful process , idCbs {} ", event.getCbsId()))
                .onFailure(error -> log.info("LoanAutomaticPayment Event was failed , idCbs {} , msg : {} ", event.getCbsId(), error.getMessage(), error));
    }

    private NotifyAutomaticPaymentRequest buildRequest(LoanAutomaticPayment event) {
        return NotifyAutomaticPaymentRequest
                .builder()
                .cbsId(event.getCbsId())
                .valuePaid(event.getValuePaid())
                .paymentStatus(event.getPaymentStatus())
                .build();
    }

    @Override
    public Class<LoanAutomaticPayment> eventClass() {
        return LoanAutomaticPayment.class;

    }
}

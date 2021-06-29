package com.lulobank.clients.services.application.usecase.loanautomaticpayment;

import com.lulobank.clients.services.application.port.in.NotifyAutomaticPaymentPort;
import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.domain.findclientbyidbsc.NotifyAutomaticPaymentRequest;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class NotifyAutomaticPaymentUseCase implements NotifyAutomaticPaymentPort {

    private final ClientsV3Repository clientsV3Repository;
    private final ClientNotifyService clientNotifyService;

    @Override
    public Try<Void> execute(NotifyAutomaticPaymentRequest command) {

        return clientsV3Repository.findByIdCbs(command.getCbsId())
                .onEmpty(() -> log.error("[NotifyAutomaticPaymentUseCase] Client not found by IdCbs"))
                .toTry()
                .peek(clientsV3Entity -> log.info("Find Client by IdCbs : {} ", clientsV3Entity.getIdClient()))
                .map(clientsV3Entity -> buildMessage(command, clientsV3Entity))
                .flatMap(clientNotifyService::automaticPayment)
                .onFailure(error -> log.error("Error trying to send messages to Notify Payment , msg {}  ", error.getMessage(), error));
    }

    private LoanAutomaticDebitMessage buildMessage(NotifyAutomaticPaymentRequest command, ClientsV3Entity clientsV3Entity) {
        return
                LoanAutomaticDebitMessage
                        .builder()
                        .idClient(clientsV3Entity.getIdClient())
                        .valuePaid(command.getValuePaid())
                        .paymentStatus(command.getPaymentStatus())
                        .creditId(command.getCbsId())
                        .email(clientsV3Entity.getEmailAddress())
                        .fullName(clientsV3Entity.getName(), clientsV3Entity.getLastName())
                        .build();
    }

}

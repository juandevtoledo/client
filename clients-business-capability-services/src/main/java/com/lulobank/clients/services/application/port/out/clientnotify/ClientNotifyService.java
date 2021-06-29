package com.lulobank.clients.services.application.port.out.clientnotify;

import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

public interface ClientNotifyService {

    Try<Void> automaticPayment(LoanAutomaticDebitMessage loanAutomaticDebitMessage);

    Try<Void> sendBlacklistNotification(ClientsV3Entity clientEntity);
}

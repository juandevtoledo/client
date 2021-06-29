package com.lulobank.clients.services.application.port.out.debitcards;

import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsError;
import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import io.vavr.control.Either;

import java.util.Map;

public interface DebitCardsPort {

    Either<DebitCardsError, DebitCard> getDebitCardByIdClient(Map<String, String> headers, String idClient);

    Either<DebitCardsError, CardStatus> getDebitCardStatusByIdClient(Map<String, String> headers, String idClient);

}

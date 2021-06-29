package com.lulobank.clients.services.application.util;

import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUtils {

    public static String hideProductNumberSavingAccount(SavingAccount savingAccount){
        return Option.of(savingAccount)
                .map(SavingAccount::getIdSavingAccount)
                .filter(number->number.length()>4)
                .map(number->number.substring(number.length()-4))
                .getOrElse(()-> Strings.EMPTY);
    }

    public static String hideProductNumberDebitCard(DebitCard debitCard){
        return Option.of(debitCard)
                .map(DebitCard::getCardNumberMask)
                .filter(number->number.length()>4)
                .map(number->number.substring(number.length()-4))
                .getOrElse(()-> Strings.EMPTY);
    }
}

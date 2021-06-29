package com.lulobank.clients.services.application.utils;

import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.services.application.util.ProductUtils;
import joptsimple.internal.Strings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductUtilsTest {

    private static final String ID_PRODUCT = "987654321";

    private SavingAccount savingsAccount;
    private DebitCard debitCard;

    @Before
    public void init(){
        savingsAccount = new SavingAccount();
        debitCard = new DebitCard();
    }

    @Test
    public void shouldReturnMaskSavingAccountProductId(){
        savingsAccount.setIdSavingAccount(ID_PRODUCT);
        assertTrue(ID_PRODUCT.endsWith(ProductUtils.hideProductNumberSavingAccount(savingsAccount)));
    }

    @Test
    public void shouldReturnEmptyWhenProcessSavingAccountProductId(){
        savingsAccount.setIdSavingAccount("123");
        assertEquals(Strings.EMPTY,ProductUtils.hideProductNumberSavingAccount(savingsAccount));
    }

    @Test
    public void shouldReturnMaskDebitCardProductId(){
        debitCard.setCardNumberMask(ID_PRODUCT);
        assertTrue(ID_PRODUCT.endsWith(ProductUtils.hideProductNumberDebitCard(debitCard)));
    }

    @Test
    public void shouldReturnEmptyWhenProcessDebitCardProductId(){
        debitCard.setCardNumberMask("123");
        assertEquals(Strings.EMPTY,ProductUtils.hideProductNumberDebitCard(debitCard));
    }


}

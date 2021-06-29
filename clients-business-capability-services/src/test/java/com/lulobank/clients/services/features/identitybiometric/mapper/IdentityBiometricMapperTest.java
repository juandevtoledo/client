package com.lulobank.clients.services.features.identitybiometric.mapper;

import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import org.junit.Test;

import static com.lulobank.clients.services.Constants.ID_TRANSACTION;
import static com.lulobank.clients.services.Sample.updateIdTransactionBuilder;
import static org.junit.Assert.assertEquals;

public class IdentityBiometricMapperTest {
    private IdentityBiometricMapper mapper = IdentityBiometricMapper.INSTANCE;

    @Test
    public void getIdentityBiometric() {
        IdentityBiometric identityBiometric = mapper.identityBiometricFrom(updateIdTransactionBuilder());
        assertEquals(ID_TRANSACTION, identityBiometric.getIdTransaction());
    }

}

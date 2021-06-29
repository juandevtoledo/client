package com.lulobank.clients.services.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EmailTest {
  private Email sut, sutE;

  @Before
  public void setup() {
    sut = new Email("email@email.com");
    sutE = new Email("email@email.com", true);
  }

  @Test
  public void should_Create_A_Not_Validated_Email_Value_Object() {
    assertNotNull(sut);
    assertFalse(sut.getVerified());
  }

  @Test
  public void should_Create_A_Validated_Email_Value_Object() {
    Email sut = new Email("email@email.com", true);
    assertNotNull(sut);
    assertTrue(sut.getVerified());
  }

  @Test
  public void
      when_Create_A_Not_Validated_Email_When_Validated_Then_Should_Be_a_Validated_Email_ValueObject() {
    sut.wasValidated();
    assertTrue(sut.getVerified());
  }

  @Test
  public void given_Two_Emails_With_Similar_Values_When_Compare_them_Then_Should_Be_Equals() {
    Email sut2 = new Email("email@email.com", true);
    assertTrue(sutE.equals(sut2));
  }

  @Test
  public void given_Two_Emails_With_Different_Values_When_Compare_them_Then_Should_Not_Be_Equals() {
    Email sut2 = new Email("email@email2.com");
    assertFalse(sut.equals(sut2));
  }

  @Test
  public void given_A_Email_When_Compare_with_differente_Type_Object_Then_Should_Not_Be_Equals() {
    Client client = new Client();
    assertFalse(sut.equals(client));
  }

  @Test
  public void given_A_Email_When_Compare_with_a_Null_Then_Should_Not_Be_Equals() {
    assertFalse(sut.equals(null));
  }
}

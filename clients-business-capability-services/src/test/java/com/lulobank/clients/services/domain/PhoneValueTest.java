package com.lulobank.clients.services.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PhoneValueTest {

  private Phone sut;

  @Before
  public void setup() {
    sut = new Phone(57, "3161234567");
  }

  @Test
  public void Should_Create_A_Not_Validated_Phone_Value_Object() {
    assertNotNull(sut);
    assertFalse(sut.getVerified());
  }

  @Test
  public void Should_Create_A_Validated_Phone_Value_Object() {
    Phone sut = new Phone(57, "3161234567", true, new Device());
    assertNotNull(sut);
    assertTrue(sut.getVerified());
  }

  @Test
  public void
      When_Create_A_Not_Validated_Phone_When_Validated_Then_Should_Be_a_Validated_Phone_ValueObject() {
    sut.wasValidated();
    assertTrue(sut.getVerified());
  }

  @Test
  public void Given_Two_Phones_With_Similar_Values_When_Compare_them_Then_Should_Be_Equals() {
    Phone sut2 = new Phone(57, "3161234567");
    assertTrue(sut.equals(sut2));
  }

  @Test
  public void Given_Two_Phones_With_Different_Values_When_Compare_them_Then_Should_Not_Be_Equals() {
    Phone sut2 = new Phone(51, "3161234567");
    assertFalse(sut.equals(sut2));
  }

  @Test
  public void Given_A_Phone_When_Compare_with_differente_Type_Object_Then_Should_Not_Be_Equals() {
    Client client = new Client();
    assertFalse(sut.equals(client));
  }

  @Test
  public void Given_A_Email_When_Compare_with_a_Null_Then_Should_Not_Be_Equals() {
    assertFalse(sut.equals(null));
  }
}

package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneDeviceCreateClientRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String iddevice;
  private String ipAddress;
  private String geolocation;
  private String country;
  private String city;
  private String mobileDevice;
  private String simCardId;
  private String model;
  private String operator;
}

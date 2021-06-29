package com.lulobank.clients.services.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Device {
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

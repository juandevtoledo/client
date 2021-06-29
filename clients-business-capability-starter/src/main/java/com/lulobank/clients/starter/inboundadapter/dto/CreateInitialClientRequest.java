package com.lulobank.clients.starter.inboundadapter.dto;

import com.lulobank.clients.services.utils.ProductTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@Accessors(chain = true)
public class CreateInitialClientRequest {

  @NotNull(message = "selectedProduct is empty or null")
  private ProductTypeEnum selectedProduct;
  @NotBlank(message = "password is empty or null")
  private String password;
  @NotNull(message = "documentAcceptancesTimestamp is empty or null")
  private Integer documentAcceptancesTimestamp;

  @Valid
  private EmailCreateClientRequest emailCreateClientRequest;
  @Valid
  private PhoneCreateInitialClient phoneCreateInitialClient;

  @Setter
  @Getter
  public static class EmailCreateClientRequest {
    @NotNull(message = "email address is empty or null")
    @Email(message = "email address is invalid")
    private String address;
    @NotNull(message = "email verified field is empty or null")
    private Boolean verified;
  }

  @Setter
  @Getter
  public static class PhoneCreateInitialClient {
    @NotNull( message = "phone prefix is empty or null")
    private Integer prefix;
    @NotNull( message = "phone number is empty or null")
    @Pattern(regexp = "\\d{8,10}", message = "phone number is invalid")
    private String number;
    @NotNull(message = "phone verified field is empty or null")
    private Boolean verified;
  }

}

package com.lulobank.clients.services.events;

import com.lulobank.core.Command;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityInformation implements Command {
  private String documentNumber;
  private String expeditionDate;
  private String documentType;
  private String birthDate;
  private String name;
  private String lastName;
  private String gender;
  private String email;
  private Phone phone;
}

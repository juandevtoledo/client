package com.lulobank.clients.v3.usecase.command;

import com.lulobank.clients.v3.vo.AdapterCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProductSaving {
    private String idClient;
    private AdapterCredentials credentials;
}

package com.lulobank.clients.starter.adapter.in.zendesk.dto;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetClientInfoByEmailAdapterResponse  extends GenericResponse {
    private Customer customer;
    private List<Product> products;
}

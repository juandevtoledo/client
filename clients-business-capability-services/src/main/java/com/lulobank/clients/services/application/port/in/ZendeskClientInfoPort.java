package com.lulobank.clients.services.application.port.in;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailRequest;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import io.vavr.control.Either;

public interface ZendeskClientInfoPort extends UseCase <GetClientInfoByEmailRequest, Either<UseCaseResponseError, GetClientInfoByEmailResponse>>{
}

package com.lulobank.clients.starter.v3.adapters.out.sqs.Common;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.API;
import io.vavr.collection.Map;

import static com.lulobank.clients.services.utils.DatesUtil.get12HourFormatTime;
import static com.lulobank.clients.services.utils.DatesUtil.getFormatLocalDate;

public class CommonSqsNotification {

    public static Map<String, Object> buildParamsBlacklistNotification(ClientsV3Entity clientEntity) {
        return API.Map("timeReport", get12HourFormatTime(clientEntity.getBlackListDate()),
                "dateReport", getFormatLocalDate(clientEntity.getBlackListDate()));
    }

    public static Map<String, Object> buildParamsPepReactivationNotification(ClientsV3Entity clientEntity) {
        return API.Map("timeReport", get12HourFormatTime(clientEntity.getDateResponsePep()),
                "dateReport", getFormatLocalDate(clientEntity.getDateResponsePep()));
    }
}
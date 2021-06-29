package com.lulobank.clients.v3.adapters.port.out.notification.report;

import io.vavr.control.Try;

public interface CreateReportNotification {

    <T> Try<Void> sendReport(String idClient, String productType, String reportType, T data);

}

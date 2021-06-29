package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

public class ApprovedRiskEngineNotificationSqsAdapterTest {
	
	private ApprovedRiskEngineNotificationSqsAdapter subject;
	@Mock
	private QueueMessagingTemplate queueMessagingTemplate;
	@Mock
	private ApprovedRiskEngineEvent approvedRiskEngineEvent;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ApprovedRiskEngineNotificationSqsAdapter(queueMessagingTemplate, approvedRiskEngineEvent);
	}
	
	@Test
	public void notifyRiskEngineResponse() {
		subject.notifyRiskEngineResponse(new ClientsV3Entity());
		verify(approvedRiskEngineEvent).send(any(), any());
	}
}

package com.lulobank.clients.starter.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.JsonExpectationsHelper;

public class LuloMockRestServer {
  private static final JsonExpectationsHelper jsonHelper = new JsonExpectationsHelper();
  private MockWebServer mockWebServer;
  private List<ExpectedRequest> expectedRequests = new ArrayList<>();

  public LuloMockRestServer(MockWebServer mockWebServer) {
    this.mockWebServer = mockWebServer;
  }

  public void enqueueGet(String url, HttpStatus httpStatus, String responseBody) {
    this.enqueue(HttpMethod.GET, url, httpStatus, responseBody, null);
  }

  public void enqueue(
      HttpMethod method,
      String url,
      HttpStatus httpStatus,
      String responseBody,
      String expectedRequestBody) {
    MockResponse mockedResponse = new MockResponse();
    mockedResponse.setResponseCode(httpStatus.value());
    mockedResponse.setBody(responseBody);
    this.mockWebServer.enqueue(mockedResponse);
    ExpectedRequest request = new ExpectedRequest();
    request.setBody(expectedRequestBody);
    request.setUrl(url);
    request.setMethod(method.name());
    this.expectedRequests.add(request);
  }

  public void validateCalls() {
    this.expectedRequests.stream()
        .forEach(
            expectedRequest -> {
              try {
                RecordedRequest recordedRequest =
                    this.mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS);
                expectedRequest.getUrl();
                recordedRequest.getPath();
                String assertionMessage = "the url's should match expected: %s got: %s";
                assertTrue(
                    String.format(
                        assertionMessage, expectedRequest.getUrl(), recordedRequest.getPath()),
                    fitsUrlPathTemplate(recordedRequest.getPath(), expectedRequest.getUrl()));
                if (!HttpMethod.GET.name().equals(recordedRequest.getMethod())) {
                  jsonHelper.assertJsonEqual(
                      expectedRequest.getBody(), recordedRequest.getUtf8Body(), false);
                }
                assertEquals(
                    "The request Method should match",
                    expectedRequest.getMethod(),
                    recordedRequest.getMethod());
              } catch (InterruptedException e) {
                assertTrue("The server no longer has queued response to return", false);
              } catch (Exception e) {
                assertTrue("Error validating the response body", false);
              }
            });
    this.reset();
  }

  public boolean fitsUrlPathTemplate(String path, String template) {
    return FileSystems.getDefault().getPathMatcher("glob:" + template).matches(Paths.get(path));
  }

  public void reset() {
    this.expectedRequests.clear();
  }

  public MockWebServer getMockWebServer() {
    return mockWebServer;
  }

  public void setMockWebServer(MockWebServer mockWebServer) {
    this.mockWebServer = mockWebServer;
  }

  public List<ExpectedRequest> getExpectedRequests() {
    return expectedRequests;
  }

  public void setExpectedRequests(List<ExpectedRequest> expectedRequests) {
    this.expectedRequests = expectedRequests;
  }
}

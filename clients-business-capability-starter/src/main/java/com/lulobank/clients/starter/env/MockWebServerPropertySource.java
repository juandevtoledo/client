package com.lulobank.clients.starter.env;

import java.io.IOException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/** @author Rob Winch */
public class MockWebServerPropertySource extends PropertySource<MockWebServer>
    implements DisposableBean {

  private static final MockResponse NOT_FOUND_RESPONSE =
      response(
          "{ \"message\" : \"This mock authorization server responds to just two requests: POST /introspect"
              + " and GET /.well-known/jwks.json.\" }",
          404);

  // jwks endpoint

  private static final MockResponse JWKS_RESPONSE =
      response(
          "{\"keys\": [{\"alg\": \"RS256\",\"kid\": \"/Ryp2e82eqttMMtY3lhKPrPcRHgNW7+1nbP/XgJB4PI=\",\"use\": \"sig\",\"kty\": \"RSA\",\"n\": \"xTo__lX8CnIUv24gUO6ycNcpmDScHsMX2pAqefCmhEx6ulOUWNdEolCPX4pc-m5KLmtVu6YQ62ewZWzROgUw3Lr3rPXa2XVM4nsqeI9qn_ZbBJZCnGFYOhuPoWJVMdF6eJYSLHy-u3TuscNmNydH-SDAdD3ZWl0TlW-orqsP-akXzzSiIYYKkLagtqJbWaJnwf_pklovPqpUfaURT9Gr4_0Fc7XAZQ3xNcz5oM_GbMdW99viw23dkqJ7Huyl2IHy7pfSSp_hR2k3vsSCaFMC61cs3xjLhrwkqj9EKqRk9ILGoDdcrTAk_T88vE1D8hwTMl6yvER9cDnAhego3h-Q2w\",\"e\": \"AQAB\"}]}",
          200);

  /** Name of the random {@link PropertySource}. */
  public static final String MOCK_WEB_SERVER_PROPERTY_SOURCE_NAME = "mockwebserver";

  private static final String NAME = "mockwebserver.url";

  private static final Log logger = LogFactory.getLog(MockWebServerPropertySource.class);

  private boolean started;

  public MockWebServerPropertySource() {
    super(MOCK_WEB_SERVER_PROPERTY_SOURCE_NAME, new MockWebServer());
  }

  @Override
  public Object getProperty(String name) {
    if (!name.equals(NAME)) {
      return null;
    }
    if (logger.isTraceEnabled()) {
      logger.trace("Looking up the url for '" + name + "'");
    }
    String url = getUrl();
    return url;
  }

  @Override
  public void destroy() throws Exception {
    getSource().shutdown();
  }

  /**
   * Get's the URL (i.e. "http://localhost:123456")
   *
   * @return
   */
  private String getUrl() {
    MockWebServer mockWebServer = getSource();
    if (!this.started) {
      intializeMockWebServer(mockWebServer);
    }
    String url = mockWebServer.url("").url().toExternalForm();
    return url.substring(0, url.length() - 1)
        .replace("view-", "")
        .replace("127" + ".0" + ".0" + ".1", "localhost");
  }

  private void intializeMockWebServer(MockWebServer mockWebServer) {
    Dispatcher dispatcher =
        new Dispatcher() {
          @Override
          public MockResponse dispatch(RecordedRequest request) {
            return doDispatch(request);
          }
        };

    mockWebServer.setDispatcher(dispatcher);
    try {
      mockWebServer.start(52231);
      this.started = true;
    } catch (IOException e) {
      throw new RuntimeException("Could not start " + mockWebServer, e);
    }
  }

  private MockResponse doDispatch(RecordedRequest request) {
    if ("/.well-known/jwks.json".equals(request.getPath())) {
      return JWKS_RESPONSE;
    }

    return NOT_FOUND_RESPONSE;
  }

  private static MockResponse response(String body, int status) {
    return new MockResponse()
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setResponseCode(status)
        .setBody(body);
  }
}

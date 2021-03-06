package io.ryos.rhino.sdk;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.ryos.rhino.sdk.simulations.ReactiveBasicHttpGetSimulation;
import io.ryos.rhino.sdk.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ReactiveBasicHttpGetTest {
  private static final String PROPERTIES_FILE = "classpath:///rhino.properties";
  private static final int PORT = 8089;

  private WireMockServer wmServer;

  @Before
  public void setUp() {
    wmServer = new WireMockServer(wireMockConfig().port(PORT)
        .jettyAcceptors(2)
        .jettyAcceptQueueSize(100)
        .containerThreads(100));
    wmServer.start();
  }

  @After
  public void tearDown() {
    wmServer.stop();
  }

  @Test
  public void testReactiveBasicHttpGet() throws InterruptedException {
    WireMock.configureFor("localhost", PORT);

    wmServer.stubFor(WireMock.post(urlEqualTo("/token"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"access_token\": \"abc123\", \"refresh_token\": \"abc123\"}")));

    wmServer.stubFor(WireMock.get(urlEqualTo("/api/files"))
        .willReturn(aResponse()
            .withFixedDelay(800)
            .withStatus(200)));

    wmServer.stubFor(WireMock.get(urlEqualTo("/api/prepare"))
        .willReturn(aResponse()
            .withFixedDelay(400)
            .withStatus(200)));

    TestUtils.overridePorts(PORT);
    Simulation.getInstance(PROPERTIES_FILE, ReactiveBasicHttpGetSimulation.class).times(50);
    Thread.sleep(5000L);
  }
}

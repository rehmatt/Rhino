package io.ryos.rhino.sdk.simulations;

import static io.ryos.rhino.sdk.dsl.DslBuilder.dsl;
import static io.ryos.rhino.sdk.dsl.MaterializableDslItem.http;
import static io.ryos.rhino.sdk.dsl.utils.DslUtils.resulting;
import static io.ryos.rhino.sdk.dsl.utils.HeaderUtils.headerValue;
import static io.ryos.rhino.sdk.utils.TestUtils.getEndpoint;

import io.ryos.rhino.sdk.SimulationConfig;
import io.ryos.rhino.sdk.annotations.Dsl;
import io.ryos.rhino.sdk.annotations.Simulation;
import io.ryos.rhino.sdk.annotations.UserRepository;
import io.ryos.rhino.sdk.dsl.DslBuilder;
import io.ryos.rhino.sdk.users.repositories.OAuthUserRepositoryFactoryImpl;
import java.util.UUID;

@Simulation(name = "Reactive Test")
@UserRepository(factory = OAuthUserRepositoryFactoryImpl.class)
public class ReactiveBasicHttpGetSimulation {

  private static final String FILES_ENDPOINT = getEndpoint("files");
  private static final String PREPARE_ENDPOINT = getEndpoint("prepare");
  private static final String X_REQUEST_ID = "X-Request-Id";
  private static final String X_API_KEY = "X-Api-Key";

  @Dsl(name = "Load DSL Request")
  public DslBuilder singleTestDsl() {
    return dsl()
        .run(http("Prepare Request")
            .header(session -> headerValue(X_REQUEST_ID, "Rhino-" + UUID.randomUUID().toString()))
            .header(X_API_KEY, SimulationConfig.getApiKey())
            .auth()
            .endpoint(PREPARE_ENDPOINT)
            .get()
            .saveTo("result"))
        .verify(http("Files Request")
            .header(session -> headerValue(X_REQUEST_ID, "Rhino-" + UUID.randomUUID().toString()))
            .header(X_API_KEY, SimulationConfig.getApiKey())
            .auth()
            .endpoint(FILES_ENDPOINT)
            .get()
                .saveTo("result"),
            resulting("201"));
  }
}

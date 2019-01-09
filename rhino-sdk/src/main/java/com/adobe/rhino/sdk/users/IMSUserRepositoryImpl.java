package com.adobe.rhino.sdk.users;

import com.adobe.rhino.sdk.SimulationConfig;
import com.adobe.rhino.sdk.data.UserSession;
import com.adobe.rhino.sdk.data.UserSessionImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IMSUserRepositoryImpl implements UserRepository<UserSession> {

  private static final Logger LOG = LogManager.getLogger(IMSUserRepositoryImpl.class);
  private static final String CLIENT_ID = "client_id";
  private static final String CLIENT_SECRET = "client_secret";
  private static final String GRANT_TYPE = "grant_type";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String SCOPE = "scope";

  private final List<UserSession> authUsers;
  private final List<User> users;
  private final ExecutorService executorService;
  private final AtomicInteger cursor = new AtomicInteger(-1);

  private final ObjectMapper objectMapper = new ObjectMapper();

  public IMSUserRepositoryImpl(final UserProvider userProvider) {
    Objects.requireNonNull(userProvider);
    this.users = userProvider.readUsers();
    this.authUsers = new ArrayList<>(users.size());
    this.executorService = Executors.newFixedThreadPool(1);
  }


  public IMSUserRepositoryImpl authenticateAll() {
    System.out.println("! Found " + users.size() + " users. Authenticating...");
    users.forEach(u -> executorService.submit(() -> {
      authUsers.add(new UserSessionImpl(authenticate(u)));
    }));

    return this;
  }

  private User authenticate(User user) {
    try {
      Form form = new Form();
      form.param(CLIENT_ID, SimulationConfig.getClientId());
      form.param(CLIENT_SECRET, SimulationConfig.getClientSecret());
      form.param(GRANT_TYPE, SimulationConfig.getGrantType());
      form.param(SCOPE, user.getScope());
      form.param(USERNAME, user.getUsername());
      form.param(PASSWORD, user.getPassword());

      final Client client = ClientBuilder.newClient();

      final Response response = client
          .target(SimulationConfig.getAuthServer())
          .request()
          .post(Entity.form(form));

      if (response.getStatus() != Status.OK.getStatusCode()) {
        LOG.error("Cannot login user:" + response.getStatus());
      }

      final String s = response.readEntity(String.class);
      final OAuthEntity o = objectMapper.readValue(s, OAuthEntity.class);

      return new OAuthUserImpl(user.getUsername(),
          user.getPassword(),
          o.getAccessToken(),
          o.getRefreshToken(),
          user.getScope(),
          SimulationConfig.getClientId(),
          user.getId());
    } catch (Exception e) {
      LOG.error(e);
    }

    return null;
  }

  @Override
  public UserSession take() {
    cursor.getAndUpdate((p) -> (p + 1) % authUsers.size());
    return authUsers.get(cursor.get());
  }

  public List<UserSession> getUserSessions() {
    return authUsers;
  }

  @Override
  public boolean has(int numberOfUsers) {
    if (users.size() < numberOfUsers) {
      throw new RuntimeException(
          "Insufficient number of users read from the source.");
    }
    return authUsers.size() >= numberOfUsers;
  }
}

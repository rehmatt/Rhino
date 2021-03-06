/*
  Copyright 2018 Ryos.io.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package io.ryos.rhino.sdk.data;

import io.ryos.rhino.sdk.users.data.User;
import java.util.Optional;

/**
 * User define is a stash to store objects and share them among scenarios per user define. A user
 * will be created before the simulation starts, and it will existing during the simulation
 * execution.
 * <p>
 *
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public interface UserSession extends Context {

  /**
   * Returns the user of the current define.
   * <p>
   *
   * @return The user of the current define.
   */
  User getUser();

  /**
   * Optional of user token.
   * <p>
   *
   * @return {@link Optional} type.
   */
  Optional<String> getUserToken();

  /**
   * Optional of refresh token.
   * <p>
   *
   * @return {@link Optional} type.
   */
  Optional<String> getRefreshToken();

  SimulationSession getSimulationSession();

  SimulationSession getSimulationSessionFor(User user);
}

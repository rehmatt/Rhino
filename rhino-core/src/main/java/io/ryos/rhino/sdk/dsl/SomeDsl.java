/*
 * Copyright 2020 Ryos.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ryos.rhino.sdk.dsl;

import io.ryos.rhino.sdk.data.UserSession;
import java.util.function.Function;

/**
 * Some spec is a custom spec to enable developers to add arbitrary code snippets into the DSL.
 * <p>
 *
 * @author Erhan Bagdemir
 * @since 1.1.0
 */
public interface SomeDsl extends MaterializableDslItem {

  /**
   * Function contains the code snippet to be applied.
   * <p>
   *
   * @return MaterializableDslItem function.
   */
  Function<UserSession, String> getFunction();

  /**
   * Method to add a spec function into the DSL.
   * <p>
   */
  MaterializableDslItem exec(Function<UserSession, String> function);
}

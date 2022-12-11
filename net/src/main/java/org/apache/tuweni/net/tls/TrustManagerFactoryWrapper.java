/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuweni.net.tls;

import java.util.function.Function;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.net.TrustOptions;

final class TrustManagerFactoryWrapper implements TrustOptions {

  private final TrustManagerFactory trustManagerFactory;

  TrustManagerFactoryWrapper(TrustManagerFactory trustManagerFactory) {
    this.trustManagerFactory = trustManagerFactory;
  }

  @Override
  public TrustOptions copy() {
    return clone();
  }

  @Override
  public TrustOptions clone() {
    return new TrustManagerFactoryWrapper(trustManagerFactory);
  }

  @Override
  public TrustManagerFactory getTrustManagerFactory(Vertx vertx) {
    return trustManagerFactory;
  }

  @Override
  public Function<String, TrustManager[]> trustManagerMapper(Vertx vertx) throws Exception {
    return (server) -> trustManagerFactory.getTrustManagers();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TrustManagerFactoryWrapper)) {
      return false;
    }
    TrustManagerFactoryWrapper other = (TrustManagerFactoryWrapper) obj;
    return trustManagerFactory.equals(other.trustManagerFactory);
  }

  @Override
  public int hashCode() {
    return trustManagerFactory.hashCode();
  }
}

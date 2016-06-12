/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.squareup.okhttp;

import libcore.net.NetworkSecurityPolicy;
import java.io.IOException;
import java.net.Proxy;
import java.net.ResponseCache;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.List;

public class HttpHandler extends URLStreamHandler {

    private final static List<ConnectionSpec> CLEARTEXT_ONLY =
        Collections.singletonList(ConnectionSpec.CLEARTEXT);

    private final ConfigAwareConnectionPool configAwareConnectionPool =
            ConfigAwareConnectionPool.getInstance();

    @Override protected URLConnection openConnection(URL url) throws IOException {
        return newOkUrlFactory(null /* proxy */).open(url);
    }

    @Override protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null || proxy == null) {
            throw new IllegalArgumentException("url == null || proxy == null");
        }
        return newOkUrlFactory(proxy).open(url);
    }

    @Override protected int getDefaultPort() {
        return 80;
    }

    protected OkUrlFactory newOkUrlFactory(Proxy proxy) {
        OkUrlFactory okUrlFactory = createHttpOkUrlFactory(proxy);
        // For HttpURLConnections created through java.net.URL Android uses a connection pool that
        // is aware when the default network changes so that pooled connections are not re-used when
        // the default network changes.
        okUrlFactory.client().setConnectionPool(configAwareConnectionPool.get());
        return okUrlFactory;
    }

    /**
     * Creates an OkHttpClient suitable for creating {@link java.net.HttpURLConnection} instances on
     * Android.
     */
    // Visible for android.net.Network.
    public static OkUrlFactory createHttpOkUrlFactory(Proxy proxy) {
        OkHttpClient client = new OkHttpClient();

        // Do not permit http -> https and https -> http redirects.
        client.setFollowSslRedirects(false);

        if (NetworkSecurityPolicy.isCleartextTrafficPermitted()) {
          // Permit cleartext traffic only (this is a handler for HTTP, not for HTTPS).
          client.setConnectionSpecs(CLEARTEXT_ONLY);
        } else {
          // Cleartext HTTP denied by policy. Make okhttp deny cleartext HTTP attempts using the
          // only mechanism it currently provides -- pretend there are no suitable routes.
          client.setConnectionSpecs(Collections.<ConnectionSpec>emptyList());
        }

        // When we do not set the Proxy explicitly OkHttp picks up a ProxySelector using
        // ProxySelector.getDefault().
        if (proxy != null) {
            client.setProxy(proxy);
        }

        // OkHttp requires that we explicitly set the response cache.
        OkUrlFactory okUrlFactory = new OkUrlFactory(client);
        ResponseCache responseCache = ResponseCache.getDefault();
        if (responseCache != null) {
            AndroidInternal.setResponseCache(okUrlFactory, responseCache);
        }
        return okUrlFactory;
    }

}

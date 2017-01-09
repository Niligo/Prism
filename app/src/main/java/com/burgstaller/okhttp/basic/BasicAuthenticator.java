package com.burgstaller.okhttp.basic;

import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Standard HTTP basic authenticator.
 */
public class BasicAuthenticator implements CachingAuthenticator {
    private static final String TAG = "OkBasic";
    private final Credentials credentials;

    public BasicAuthenticator(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        final Request request = response.request();
        return authFromRequest(request);
    }

    private Request authFromRequest(Request request) {
        // prevent infinite loops when the password is wrong
        final String authorizationHeader = request.header("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic")) {
            return null;
        }
        String authValue = okhttp3.Credentials.basic(credentials.getUserName(), credentials.getPassword());
        return request.newBuilder()
                .header("Authorization", authValue)
                .build();
    }

    @Override
    public Request authenticateWithState(Request request) throws IOException {
        return authFromRequest(request);
    }
}

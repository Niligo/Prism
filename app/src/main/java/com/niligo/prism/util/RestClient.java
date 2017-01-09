package com.niligo.prism.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.DispatchingAuthenticator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class RestClient {


    private static RestClient instance = null;


    public static RestClient getInstance()
    {
        if (instance == null)
        {
            instance = new RestClient();
        }
        return instance;
    }

    private RestClient() {

    }

    public String runGet(String url, String username, String password) {

        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
        Credentials credentials = new Credentials(username, password);
        DigestAuthenticator digestAuthenticator = new DigestAuthenticator(credentials);
        DispatchingAuthenticator authenticator = new DispatchingAuthenticator.Builder()
                .with("digest", digestAuthenticator)
                .build();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
                .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                .addInterceptor(new AuthenticationCacheInterceptor(authCache));

        OkHttpClient client = clientBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return "";
    }

    public Response runPost(String url, RequestBody requestBody) {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS);

        OkHttpClient client = clientBuilder.build();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(requestBody);
        Request request = builder.build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return null;
    }
}
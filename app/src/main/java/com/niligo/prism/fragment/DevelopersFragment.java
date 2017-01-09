package com.niligo.prism.fragment;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.niligo.prism.Constants;
import com.niligo.prism.R;
import com.niligo.prism.activity.MainActivity;
import com.niligo.prism.util.FakeX509TrustManager;


public class DevelopersFragment extends BaseFragment {

    private WebView webView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_developers, container, false);

        webView = (WebView) view.findViewById(R.id.webView);

        System.setProperty("http.keepAlive", "false");
        FakeX509TrustManager.allowAllSSL();

        webView.getSettings().setJavaScriptEnabled(true);
        //webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webView.loadUrl(Constants.DEVELOPERS_URL);


        ((MainActivity) getActivity()).setOnBackPressedListener(null);
        return view;
    }
}


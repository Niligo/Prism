package com.niligo.prism.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.callback.ForgotPassCallback;
import com.niligo.prism.callback.LoginCallback;
import com.niligo.prism.callback.RegisterCallback;
import com.niligo.prism.model.ForgotPassResponseBean;
import com.niligo.prism.model.LoginResponseBean;
import com.niligo.prism.model.RegisterResponseBean;
import com.niligo.prism.util.Utils;
import com.niligo.prism.widget.NPTextView;
import com.squareup.picasso.Picasso;

import org.fourthline.cling.Main;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by mahdi on 7/18/2016 AD.
 */
public class LoginActivity extends AppCompatActivity implements RegisterCallback, LoginCallback, ForgotPassCallback{

    private RelativeLayout login_layout;

    private static final int RC_SIGN_IN = 1000;
    //private GoogleApiClient mGoogleApiClient;
    private Dialog forgot_pass_dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        login_layout = (RelativeLayout) findViewById(R.id.root);

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e("Tag", "onconectinfailed" + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        showLogin();

    }

    private void showLogin()
    {
        final View view = View.inflate(this, R.layout.activity_login, null);

        TextInputLayout userTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_input);
        TextInputLayout passTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_input2);
        final TextInputEditText userTextInputEditText = (TextInputEditText) view.findViewById(R.id.edittext);
        final TextInputEditText passTextInputEditText = (TextInputEditText) view.findViewById(R.id.edittext2);
        userTextInputLayout.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        passTextInputLayout.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        userTextInputEditText.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        passTextInputEditText.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        NPTextView register = (NPTextView) view.findViewById(R.id.register);
        NPTextView forgot_password = (NPTextView) view.findViewById(R.id.forgot_password);
        NPTextView get_new = (NPTextView) view.findViewById(R.id.get_prism);
        RelativeLayout login_google = (RelativeLayout) view.findViewById(R.id.login_google);
        RelativeLayout login = (RelativeLayout) view.findViewById(R.id.login);

        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);*/
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userTextInputEditText.getText().toString();
                String password = passTextInputEditText.getText().toString();
                if (username.length() == 0)
                {
                    Utils.setError(getString(R.string.error_empty), userTextInputEditText);
                    return;
                }
                if (!Utils.isEmailValid(username))
                {
                    Utils.setError(getString(R.string.error_invalid_email), userTextInputEditText);
                    return;
                }

                if (password.length() == 0)
                {
                    Utils.setError(getString(R.string.error_empty), passTextInputEditText);
                    return;
                }

                Utils.showWaiting(LoginActivity.this);
                ServerCoordinator.getInstance().login(username,
                        password,
                        LoginActivity.this);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegister();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (forgot_pass_dialog != null)
                {
                    forgot_pass_dialog.dismiss();
                    forgot_pass_dialog = null;
                }
                forgot_pass_dialog = new Dialog(LoginActivity.this, R.style.Theme_Dialog);
                forgot_pass_dialog.setContentView(R.layout.dialog_forgot_password);
                forgot_pass_dialog.setCancelable(true);
                forgot_pass_dialog.show();
                final EditText email_et = (EditText) forgot_pass_dialog.findViewById(R.id.edittext2);
                RelativeLayout config = (RelativeLayout) forgot_pass_dialog.findViewById(R.id.config);
                config.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = email_et.getText().toString().trim();
                        if (email.length() == 0)
                        {
                            Utils.setError(getString(R.string.error_empty), email_et);
                            return;
                        }
                        if (!Utils.isEmailValid(email))
                        {
                            Utils.setError(getString(R.string.error_invalid_email), email_et);
                            return;
                        }

                        Utils.showWaiting(LoginActivity.this);
                        ServerCoordinator.getInstance().forgotPassword(email,
                                LoginActivity.this);
                    }
                });
                forgot_pass_dialog.getWindow().setLayout(Utils.dp2px(LoginActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                forgot_pass_dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });

        get_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://niligo.com/prism"));
                startActivity(browserIntent);
            }
        });
        login_layout.removeAllViews();
        login_layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void showRegister()
    {
        final View view = View.inflate(this, R.layout.activity_register, null);

        TextInputLayout userTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_input);
        TextInputLayout passTextInputLayout = (TextInputLayout) view.findViewById(R.id.text_input2);
        final TextInputEditText userTextInputEditText = (TextInputEditText) view.findViewById(R.id.edittext);
        final TextInputEditText passTextInputEditText = (TextInputEditText) view.findViewById(R.id.edittext2);
        userTextInputLayout.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        passTextInputLayout.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        userTextInputEditText.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        passTextInputEditText.setTypeface(NiligoPrismApplication.getInstance().getTypeface());
        NPTextView register = (NPTextView) view.findViewById(R.id.register);
        NPTextView get_new = (NPTextView) view.findViewById(R.id.get_prism);
        RelativeLayout login = (RelativeLayout) view.findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userTextInputEditText.getText().toString();
                String password = passTextInputEditText.getText().toString();
                if (username.length() == 0)
                {
                    Utils.setError(getString(R.string.error_empty), userTextInputEditText);
                    return;
                }
                if (!Utils.isEmailValid(username))
                {
                    Utils.setError(getString(R.string.error_invalid_email), userTextInputEditText);
                    return;
                }

                if (password.length() == 0)
                {
                    Utils.setError(getString(R.string.error_empty), passTextInputEditText);
                    return;
                }

                Utils.showWaiting(LoginActivity.this);
                ServerCoordinator.getInstance().signup(username,
                        password,
                        false,
                        LoginActivity.this);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogin();
            }
        });
        get_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://niligo.com/prism"));
                startActivity(browserIntent);
            }
        });

        login_layout.removeAllViews();
        login_layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.e("Tag", "signed in");
                Log.e("tag", "email = " + acct.getEmail());
                Utils.showToast(this, R.string.success_login_google, Toast.LENGTH_SHORT);
                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else {
                Log.e("Tag", "signed out");
            }
        }*/
    }

    @Override
    public void registerSuccess(final String email,
                                final String password,
                                final RegisterResponseBean registerResponseBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                if (registerResponseBean.getError_code() == 0)
                {
                    Utils.showToast(LoginActivity.this, R.string.success_register, Toast.LENGTH_LONG);
                    SharedPreferences.Editor editor = NiligoPrismApplication.getInstance().getSharedPreferences().edit();
                    editor.putString(Constants.PREF_EMAIL, email);
                    editor.putString(Constants.PREF_PASSWORD, password);
                    editor.putString(Constants.PREF_TOKEN, registerResponseBean.getToken());
                    editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
                    editor.apply();
                    finish();
                }
                else
                {
                    if (registerResponseBean.getError_message() != null && !registerResponseBean.getError_message().equals("null"))
                    {
                        Utils.showToast(LoginActivity.this, registerResponseBean.getError_message(), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Utils.handleError(registerResponseBean.getError_code(), LoginActivity.this);
                    }
                }

            }
        });
    }

    @Override
    public void registerFailure(final int error_code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                switch (error_code)
                {
                    case 401:
                        Utils.showToast(LoginActivity.this, R.string.error_unuth, Toast.LENGTH_LONG);
                        break;

                    case 500:
                        Utils.showToast(LoginActivity.this, R.string.error_connection, Toast.LENGTH_LONG);
                        break;
                }
            }
        });

    }

    @Override
    public void loginSuccess(final String email,
                             final String password,
                             final LoginResponseBean loginResponseBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                if (loginResponseBean.getError_code() == 0)
                {
                    Utils.showToast(LoginActivity.this, R.string.success_register, Toast.LENGTH_LONG);
                    SharedPreferences.Editor editor = NiligoPrismApplication.getInstance().getSharedPreferences().edit();
                    editor.putString(Constants.PREF_EMAIL, email);
                    editor.putString(Constants.PREF_PASSWORD, password);
                    editor.putString(Constants.PREF_TOKEN, loginResponseBean.getToken());
                    editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
                    editor.apply();
                    finish();
                }
                else
                {
                    if (loginResponseBean.getError_message() != null && !loginResponseBean.getError_message().equals("null"))
                    {
                        Utils.showToast(LoginActivity.this, loginResponseBean.getError_message(), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Utils.handleError(loginResponseBean.getError_code(), LoginActivity.this);
                    }
                }

            }
        });
    }

    @Override
    public void loginFailure(final int error_code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                switch (error_code)
                {
                    case 401:
                        Utils.showToast(LoginActivity.this, R.string.error_unuth, Toast.LENGTH_LONG);
                        break;

                    case 500:
                        Utils.showToast(LoginActivity.this, R.string.error_connection, Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }

    @Override
    public void forgotPassSuccess(final ForgotPassResponseBean forgotPassResponseBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                if (forgotPassResponseBean.getError_code() == 0)
                {
                    if (forgot_pass_dialog != null)
                    {
                        forgot_pass_dialog.dismiss();
                        forgot_pass_dialog = null;
                    }
                    Utils.showToast(LoginActivity.this, R.string.success_forgot_pass, Toast.LENGTH_LONG);
                }
                else
                {
                    if (forgotPassResponseBean.getError_message() != null && !forgotPassResponseBean.getError_message().equals("null"))
                    {
                        Utils.showToast(LoginActivity.this, forgotPassResponseBean.getError_message(), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Utils.handleError(forgotPassResponseBean.getError_code(), LoginActivity.this);
                    }
                }

            }
        });
    }

    @Override
    public void forgotPassFailure(final int error_code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideWaiting();
                switch (error_code)
                {
                    case 401:
                        Utils.showToast(LoginActivity.this, R.string.error_unuth, Toast.LENGTH_LONG);
                        break;

                    case 500:
                        Utils.showToast(LoginActivity.this, R.string.error_connection, Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }
}

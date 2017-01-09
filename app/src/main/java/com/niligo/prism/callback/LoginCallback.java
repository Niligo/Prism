package com.niligo.prism.callback;

import com.niligo.prism.model.LoginResponseBean;
import com.niligo.prism.model.RegisterResponseBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface LoginCallback {
    public void loginSuccess(String email, String password, LoginResponseBean loginResponseBean);
    public void loginFailure(int error_code);
}

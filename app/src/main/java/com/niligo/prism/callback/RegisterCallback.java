package com.niligo.prism.callback;

import com.niligo.prism.model.ColorBean;
import com.niligo.prism.model.RegisterResponseBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface RegisterCallback {
    public void registerSuccess(String email, String password, RegisterResponseBean registerResponseBean);
    public void registerFailure(int error_code);
}

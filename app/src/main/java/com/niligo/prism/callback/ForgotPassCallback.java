package com.niligo.prism.callback;

import com.niligo.prism.model.ForgotPassResponseBean;
import com.niligo.prism.model.RegisterResponseBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface ForgotPassCallback {
    public void forgotPassSuccess(ForgotPassResponseBean forgotPassResponseBean);
    public void forgotPassFailure(int error_code);
}

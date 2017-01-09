package com.niligo.prism.callback;

import com.niligo.prism.model.ForgotPassResponseBean;
import com.niligo.prism.model.InviteResponseBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface InviteCallback {
    public void inviteSuccess(InviteResponseBean inviteResponseBean);
    public void inviteFailure(int error_code);
}

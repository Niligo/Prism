package com.niligo.prism.callback;

import com.niligo.prism.model.ColorBean;

/**
 * Created by mahdi on 1/8/17.
 */

public interface GetPowerCallback {
    public void getPowerSuccess(boolean power);
    public void getPowerFailure(int error_code);
}

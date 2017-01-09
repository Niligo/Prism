package com.niligo.prism.callback;

import com.niligo.prism.model.ColorBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface GetColorCallback {
    public void getColorSuccess(ColorBean colorBean);
    public void getColorFailure(int error_code);
}

package com.niligo.prism.callback;

import com.niligo.prism.model.BackupResponseBean;
import com.niligo.prism.model.InviteResponseBean;

/**
 * Created by mahdi on 8/2/16.
 */
public interface BackupCallback {
    public void backupSuccess(BackupResponseBean backupResponseBean);
    public void backupFailure(int error_code);
}

package com.niligo.prism.util;

import com.niligo.prism.model.BackupResponseBean;
import com.niligo.prism.model.ForgotPassResponseBean;
import com.niligo.prism.model.InviteResponseBean;
import com.niligo.prism.model.LoginResponseBean;
import com.niligo.prism.model.MethodName;
import com.niligo.prism.model.RegisterResponseBean;
import com.niligo.prism.model.ResponseBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by mahdi on 2/4/16 AD.
 */
public class JsonParser {

    private static JsonParser instance = null;

    private JsonParser() {

    }

    public static JsonParser getInstance() {
        if (instance == null)
            instance = new JsonParser();

        return instance;
    }

    public ResponseBean parse(String response, MethodName methodName) {

        ResponseBean responseBean = null;

        switch (methodName)
        {
            case REGISTER:
            {
                responseBean = parseRegister(response);
                break;
            }
            case LOGIN:
            {
                responseBean = parseLogin(response);
                break;
            }
            case FORGOT_PASS:
            {
                responseBean = parseForgotPass(response);
                break;
            }
            case INVITE:
            {
                responseBean = parseInvite(response);
                break;
            }
            case BACKUP:
            {
                responseBean = parseBackup(response);
                break;
            }
        }

        return responseBean;
    }


    private RegisterResponseBean parseRegister(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            String error_message = jsonObject.getString("error_message");
            String token = jsonObject.getString("token");

            return new RegisterResponseBean(error_code,
                    error_message,
                    token);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private LoginResponseBean parseLogin(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            String error_message = jsonObject.getString("error_message");
            String token = jsonObject.getString("token");

            return new LoginResponseBean(error_code,
                    error_message,
                    token);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private ForgotPassResponseBean parseForgotPass(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            String error_message = jsonObject.getString("error_message");

            return new ForgotPassResponseBean(error_code,
                    error_message);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private InviteResponseBean parseInvite(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            String error_message = jsonObject.getString("error_message");

            return new InviteResponseBean(error_code,
                    error_message);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private BackupResponseBean parseBackup(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            int error_code = jsonObject.getInt("error_code");
            String error_message = jsonObject.getString("error_message");

            return new BackupResponseBean(error_code,
                    error_message);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
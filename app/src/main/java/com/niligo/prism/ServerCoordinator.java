package com.niligo.prism;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.niligo.prism.callback.BackupCallback;
import com.niligo.prism.callback.ForgotPassCallback;
import com.niligo.prism.callback.GetColorCallback;
import com.niligo.prism.callback.InviteCallback;
import com.niligo.prism.callback.LoginCallback;
import com.niligo.prism.callback.RegisterCallback;
import com.niligo.prism.model.BackupResponseBean;
import com.niligo.prism.model.ColorBean;
import com.niligo.prism.model.ForgotPassResponseBean;
import com.niligo.prism.model.InviteResponseBean;
import com.niligo.prism.model.LoginResponseBean;
import com.niligo.prism.model.MethodName;
import com.niligo.prism.model.RegisterResponseBean;
import com.niligo.prism.util.JsonParser;
import com.niligo.prism.util.RestClient;
import com.niligo.prism.util.Utils;

import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mahdi on 8/5/16.
 */
public class ServerCoordinator {

    private static ServerCoordinator instance = null;
    private String username, password, webservice;
    private static final int retry = 10;
    private final String MEDIA_TYPE = "application/json; charset=utf-8";
    private ThreadPoolExecutor threadPoolExecutor;

    private ServerCoordinator()
    {
        threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Log.e("Tag", "new Thread created");
                return new Thread(r, "Thread No : " + threadNumber.getAndIncrement());
            }
        });
    }

    public static ServerCoordinator getInstance()
    {
        if (instance == null)
            instance = new ServerCoordinator();

        return instance;
    }

    public ServerCoordinator setBulbCredentials(String username, String password, String webservice)
    {
        this.username = username;
        this.password = password;
        this.webservice = webservice;
        return this;
    }

    private void submit(Runnable task)
    {
        if(!threadPoolExecutor.isShutdown() && threadPoolExecutor.getActiveCount() != threadPoolExecutor.getMaximumPoolSize()){
            try {
                threadPoolExecutor.submit(task);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                new Thread(task).start();
            }
        } else {
            new Thread(task).start();
        }
    }

    public void state(final String color,
                      final int fade) {


        submit(new Runnable() {
            @Override
            public void run() {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                NiligoPrismApplication.getInstance().getActivityManager().getMemoryInfo(memoryInfo);
                if (memoryInfo.availMem <= memoryInfo.threshold || memoryInfo.lowMemory)
                {
                    Log.e("tag", "gc");
                    System.gc();
                }
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("color", color));
                wsParameters.add(new Utils.WSParameter("fade", fade));
                wsParameters.add(new Utils.WSParameter("power", 1));
                RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "state", wsParameters),
                        username,
                        password);
            }
        });
    }

    public void setPower(final boolean power) {


        submit(new Runnable() {
            @Override
            public void run() {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                NiligoPrismApplication.getInstance().getActivityManager().getMemoryInfo(memoryInfo);
                if (memoryInfo.availMem <= memoryInfo.threshold || memoryInfo.lowMemory)
                {
                    System.gc();
                }
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("fade", 1));
                wsParameters.add(new Utils.WSParameter("power", power ? 1 : 0));
                RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "state", wsParameters),
                        username,
                        password);
            }
        });


    }

    public void schedule(final int diff, final boolean power, final String color) {

        submit(new Runnable() {
            @Override
            public void run() {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                NiligoPrismApplication.getInstance().getActivityManager().getMemoryInfo(memoryInfo);
                if (memoryInfo.availMem <= memoryInfo.threshold || memoryInfo.lowMemory)
                {
                    System.gc();
                }
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("color", color));
                wsParameters.add(new Utils.WSParameter("time", diff));
                wsParameters.add(new Utils.WSParameter("power", power ? 1 : 0));
                wsParameters.add(new Utils.WSParameter("fade", Constants.FADE));
                RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "sched", wsParameters),
                        username,
                        password);
            }
        });


    }

    public void setNetworkSTA(final String ssid, final String sta_password) {

        submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("mode", "sta"));
                wsParameters.add(new Utils.WSParameter("sta.ssid", ssid));
                wsParameters.add(new Utils.WSParameter("sta.password", sta_password));
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "network", wsParameters),
                            username,
                            password);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public void setUsernamePassword(final String new_username, final String new_password) {

        submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("username", new_username));
                wsParameters.add(new Utils.WSParameter("password", new_password));
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "device", wsParameters),
                            username,
                            password);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public void reset() {

        submit(new Runnable() {
            @Override
            public void run() {
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "factory", null),
                            username,
                            password);

                    Log.e("tag", "result = " + result);

                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);

                reboot();
            }
        });


    }

    public void reboot() {

        submit(new Runnable() {
            @Override
            public void run() {
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "reboot", null),
                            username,
                            password);

                    Log.e("tag", "result = " + result);

                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });


    }

    public void update() {

        submit(new Runnable() {
            @Override
            public void run() {
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "flash", null),
                            username,
                            password);

                    Log.e("tag", "result = " + result);

                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public void notifyBulb() {

        submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("color1", "000000"));
                wsParameters.add(new Utils.WSParameter("fade1", 10));
                wsParameters.add(new Utils.WSParameter("delay1", 10));
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "notify", wsParameters),
                            username,
                            password);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public void renameBulb(final String name) {

        submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                String url_name = URLEncoder.encode(name);
                wsParameters.add(new Utils.WSParameter("name", url_name));
                wsParameters.add(new Utils.WSParameter("description", url_name));
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "device", wsParameters),
                            username,
                            password);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public void getBulb() {

        submit(new Runnable() {
            @Override
            public void run() {
                String result = "";
                int i = retry;
                do {
                    result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "device", null),
                            username,
                            password);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((result.equals("") || result.equals("Not Authorized")) && i >= 0);
            }
        });

    }

    public class Color implements Callable<ColorBean>
    {
        @Override
        public ColorBean call() throws Exception {
            String result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "state", null),
                    username,
                    password);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject current = jsonObject.getJSONObject("current");
                String color = current.getString("color");
                int fade = current.getInt("fade");
                int power = current.getInt("power");
                return new ColorBean(color,
                        fade,
                        power);
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
            return null;
        }
    }

    public ColorBean getColor()
    {
        Future<ColorBean> task = threadPoolExecutor.submit(new Color());
        try {
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*public void getColor(final GetColorCallback getColorCallback) {

        submit(new Runnable() {
            @Override
            public void run() {
                String result = RestClient.getInstance().runGet(Utils.getURLWithParams(webservice + "state", null),
                        username,
                        password);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject current = jsonObject.getJSONObject("current");
                    String color = current.getString("color");
                    int fade = current.getInt("fade");
                    int power = current.getInt("power");
                    getColorCallback.getColorSuccess(new ColorBean(color,
                            fade,
                            power)
                    );
                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    getColorCallback.getColorFailure(500);
                }
            }
        });
    }*/

    public void signup(final String username,
                       final String password,
                       final boolean isAuth,
                       final RegisterCallback registerCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("Email", username));
                wsParameters.add(new Utils.WSParameter("Password", password));
                wsParameters.add(new Utils.WSParameter("firstName", ""));
                wsParameters.add(new Utils.WSParameter("lastName", ""));
                wsParameters.add(new Utils.WSParameter("Ip", Utils.getIPAddress()));
                wsParameters.add(new Utils.WSParameter("appVer", NiligoPrismApplication.getInstance().getVersionName()));
                wsParameters.add(new Utils.WSParameter("CellNo", ""));
                wsParameters.add(new Utils.WSParameter("appName", "prism"));
                wsParameters.add(new Utils.WSParameter("Os", "Android"));
                wsParameters.add(new Utils.WSParameter("osVer", Build.VERSION.SDK_INT));
                RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), "");
                String ws = Utils.getURLWithParams(Constants.WS + "auth/register", wsParameters);
                Response response = RestClient.getInstance().runPost(ws, requestBody);
                String resp = "";
                try {
                    resp = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("tag", "resp = " + resp);
                RegisterResponseBean registerResponseBean = (RegisterResponseBean) JsonParser.getInstance().parse(resp, MethodName.REGISTER);
                if (registerResponseBean != null)
                {
                    registerCallback.registerSuccess(username, password, registerResponseBean);
                }
                else
                {
                    registerCallback.registerFailure(500);
                }


            }
        }).start();
    }

    public void login(final String username,
                       final String password,
                       final LoginCallback loginCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("Email", username));
                wsParameters.add(new Utils.WSParameter("Password", password));
                RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), "");
                String ws = Utils.getURLWithParams(Constants.WS + "auth/signIn", wsParameters);
                Response response = RestClient.getInstance().runPost(ws, requestBody);
                String resp = "";
                try {
                    resp = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("tag", "resp = " + resp);
                LoginResponseBean loginResponseBean = (LoginResponseBean) JsonParser.getInstance().parse(resp, MethodName.LOGIN);
                if (loginResponseBean != null)
                {
                    loginCallback.loginSuccess(username, password, loginResponseBean);
                }
                else
                {
                    loginCallback.loginFailure(500);
                }


            }
        }).start();
    }
    public void forgotPassword(final String username,
                               final ForgotPassCallback forgotPassCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("Email", username));
                RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), "");
                String ws = Utils.getURLWithParams(Constants.WS + "auth/resetPassword", wsParameters);
                Response response = RestClient.getInstance().runPost(ws, requestBody);
                String resp = "";
                try {
                    resp = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("tag", "resp = " + resp);
                ForgotPassResponseBean forgotPassResponseBean = (ForgotPassResponseBean) JsonParser.getInstance().parse(resp, MethodName.FORGOT_PASS);
                if (forgotPassResponseBean != null)
                {
                    forgotPassCallback.forgotPassSuccess(forgotPassResponseBean);
                }
                else
                {
                    forgotPassCallback.forgotPassFailure(500);
                }


            }
        }).start();
    }

    public void invite(final String username,
                       final InviteCallback inviteCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                wsParameters.add(new Utils.WSParameter("Email", username));
                String token = NiligoPrismApplication.getInstance().getSharedPreferences().getString(Constants.PREF_TOKEN, "");
                if (token.length() == 0)
                {
                    inviteCallback.inviteFailure(401);
                    return;
                }
                Log.e("tag", "token = " + token);
                wsParameters.add(new Utils.WSParameter("Token", token));
                RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), "");
                String ws = Utils.getURLWithParams(Constants.WS + "auth/invite", wsParameters);
                Response response = RestClient.getInstance().runPost(ws, requestBody);
                String resp = "";
                try {
                    resp = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("tag", "resp = " + resp);
                InviteResponseBean inviteResponseBean = (InviteResponseBean) JsonParser.getInstance().parse(resp, MethodName.INVITE);
                if (inviteResponseBean != null)
                {
                    inviteCallback.inviteSuccess(inviteResponseBean);
                }
                else
                {
                    inviteCallback.inviteFailure(500);
                }


            }
        }).start();
    }

    public void backup(final String value,
                       final BackupCallback backupCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Utils.WSParameter> wsParameters = new ArrayList<>();
                String token = NiligoPrismApplication.getInstance().getSharedPreferences().getString(Constants.PREF_TOKEN, "");
                if (token.length() == 0)
                {
                    backupCallback.backupFailure(401);
                    return;
                }
                Log.e("tag", "token = " + token);
                wsParameters.add(new Utils.WSParameter("Token", token));
                wsParameters.add(new Utils.WSParameter("Value", value));
                RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), "");
                String ws = Utils.getURLWithParams(Constants.WS + "backups/save", wsParameters);
                Response response = RestClient.getInstance().runPost(ws, requestBody);
                String resp = "";
                try {
                    resp = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("tag", "resp = " + resp);
                BackupResponseBean backupResponseBean = (BackupResponseBean) JsonParser.getInstance().parse(resp, MethodName.BACKUP);
                if (backupResponseBean != null)
                {
                    backupCallback.backupSuccess(backupResponseBean);
                }
                else
                {
                    backupCallback.backupFailure(500);
                }


            }
        }).start();
    }
}

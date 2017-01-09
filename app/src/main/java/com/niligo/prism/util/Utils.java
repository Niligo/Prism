package com.niligo.prism.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.niligo.prism.Constants;
import com.niligo.prism.NiligoPrismApplication;
import com.niligo.prism.R;
import com.niligo.prism.model.ColorFaveBean;
import com.niligo.prism.model.ColorFavesBean;
import com.niligo.prism.model.GroupType;
import com.niligo.prism.widget.NPTextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class Utils {

    private static Dialog view;
    private static final int MAX_PRIORITY = 999999;
    private static WifiManager wifiManager;

    public static void showWaiting(Context context)
    {
        view = new Dialog(context, R.style.Theme_Dialog);
        view.setContentView(R.layout.dialog_waiting);
        view.setCancelable(false);
        view.show();
        GifImageView gif = (GifImageView) view.findViewById(R.id.gif);
        try {
            GifDrawable gifFromAssets = new GifDrawable(context.getResources().getAssets(), "loading.gif");
            gifFromAssets.setLoopCount(0);
            gif.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.getWindow().setLayout(Utils.dp2px(context), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getWindow().setGravity(Gravity.CENTER);
    }

    public static void hideWaiting()
    {
        if (view != null && view.isShowing())
        {
            view.dismiss();
            view = null;
        }
    }

    public static int dp2px(Context context) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constants.DIALOG_WIDTH,
                context.getResources().getDisplayMetrics());
    }

    public static String amountFormater(String amount)
    {
        if (amount.length() > 0) {

            boolean isminus = amount.startsWith("-");
            amount = amount.replaceAll("[^\\d]", "");
            for (int i = amount.length() - 3; i > 0; i -= 3) {
                amount = amount.substring(0, i) + "," + amount.substring(i);
            }
            if (isminus) {
                amount = "-" + amount;
            }
            return amount;
        } else {
            return "";
        }
    }


    public static void setError(String s, EditText editText)
    {
        SpannableString title = new SpannableString(Html.fromHtml("<font color='red'>" + s + "</font>"));
        title.setSpan(new TypefaceSpan(), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setError(title);
        editText.requestFocus();
    }

    public static String clear(String amount)
    {
        if (amount != null && amount.length() > 0) {

            boolean isminus = amount.startsWith("-");

            amount = amount.replaceAll("[^\\d]", "");
            if (isminus) {
                amount = "-" + amount;
            }

            return amount;
        } else {
            return "";
        }
    }

    public static String createJson(ArrayList<WSParameter> wsParameters) {

        JSONObject jsonObject = new JSONObject();
        try
        {
            for (WSParameter wsParameter : wsParameters)
            {
                jsonObject.put(wsParameter.key, wsParameter.value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (isIPv4)
                            return sAddr;

                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }


    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String cardFormater(String cardNumber) {
        StringBuilder str = new StringBuilder(cardNumber);
        int idx = 4;
        while (idx < str.length()) {
            str.insert(idx, " ");
            idx = idx + 5;
        }
        return str.toString();
    }

    public static String faxFormater(String faxNumber) {
        StringBuilder str = new StringBuilder(faxNumber);
        int idx = 3;
        if (idx < str.length()) {
            str.insert(idx, "-");
        }
        return str.toString();
    }


    public static void showToast(final Activity context, final int res, final int length) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(context);
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.toast_layout));
                if (layout != null) {
                    NPTextView text = (NPTextView) layout.findViewById(R.id.toast_text);
                    text.setText(context.getString(res));
                    Toast toast = new Toast(context);
                    toast.setDuration(length);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });
    }

    public static void showToast(final Activity context, final String mes, final int length) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(context);
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.toast_layout));
                if (layout != null) {
                    NPTextView text = (NPTextView) layout.findViewById(R.id.toast_text);
                    text.setText(mes);
                    Toast toast = new Toast(context);
                    toast.setDuration(length);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

    }

    public static boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }


    public static void shareTextUrl(Context context, String subject, String text) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
    }

    public static Intent hasApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        return manager.getLaunchIntentForPackage(packageName);
    }


    public static void getTotalHeightofListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null)
            return;

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /*public static void showPermissionDeniedDialog(Activity activity) {
        AlertDialog.Builder alert1 = new AlertDialog.Builder(activity);
        View view1 = View.inflate(activity, R.layout.dialog_permission_denied_nerver_ask, null);
        alert1.setView(view1);
        alert1.setCancelable(true);
        AlertDialog alertDialog1 = alert1.create();
        if (!activity.isFinishing())
            alertDialog1.show();
    }*/

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentDateTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateTime = String.format(Locale.US, "%d/%02d/%02d %02d:%02d'",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        return dateTime;
    }

    public static String generateString()
    {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static boolean enableNetwork(WifiManager wifiManager, String ssid) {
        Utils.wifiManager = wifiManager;
        boolean state = false;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        if(list != null && list.size() > 0) {
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals(convertToQuotedString(ssid))) {

                    wifiManager.disconnect();

                    int newPri = getMaxPriority() + 1;
                    if(newPri >= MAX_PRIORITY) {
                        // We have reached a rare situation.
                        newPri = shiftPriorityAndSave();
                    }

                    i.priority = newPri;
                    wifiManager.updateNetwork(i);
                    wifiManager.saveConfiguration();

                    state = wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }

        return state;
    }

    private static int getMaxPriority() {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        int pri = 0;
        for (final WifiConfiguration config : configurations) {
            if (config.priority > pri) {
                pri = config.priority;
            }
        }
        return pri;
    }

    private static void sortByPriority(final List<WifiConfiguration> configurations) {
        Collections.sort(configurations,
                new Comparator<WifiConfiguration>() {
                    @Override
                    public int compare(WifiConfiguration object1, WifiConfiguration object2) {
                        return object1.priority - object2.priority;
                    }
                });
    }

    private static int shiftPriorityAndSave() {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        sortByPriority(configurations);
        final int size = configurations.size();
        for (int i = 0; i < size; i++) {
            final WifiConfiguration config = configurations.get(i);
            config.priority = i;
            wifiManager.updateNetwork(config);
        }
        wifiManager.saveConfiguration();
        return size;
    }

    private static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        final int lastPos = string.length() - 1;
        if (lastPos > 0
                && (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }

        return "\"" + string + "\"";
    }

    public static void saveColorsToSharedPref(ColorFavesBean colorFavesBean)
    {
        String element = new GsonBuilder().create().toJson(colorFavesBean);
        NiligoPrismApplication.getInstance().getSharedPreferences().edit().putString(Constants.PREF_FAV_COLORS, element).apply();
    }

    public static ColorFavesBean getColorsFromSharedPref()
    {
        String element = NiligoPrismApplication.getInstance().getSharedPreferences().getString(Constants.PREF_FAV_COLORS, null);
        if (element != null)
            return new GsonBuilder().create().fromJson(element, ColorFavesBean.class);

        return null;
    }

    public static int findFavPos() {
        ColorFavesBean colorFavesBean = getColorsFromSharedPref();
        if (colorFavesBean == null)
            return 0;

        if (colorFavesBean.getFave1() == null)
            return 0;
        if (colorFavesBean.getFave2() == null)
            return 1;
        if (colorFavesBean.getFave3() == null)
            return 2;
        if (colorFavesBean.getFave4() == null)
            return 3;
        if (colorFavesBean.getFave5() == null)
            return 4;

        long[] dates = new long[5];
        dates[0] = colorFavesBean.getFave1().getDate();
        dates[1] = colorFavesBean.getFave2().getDate();
        dates[2] = colorFavesBean.getFave3().getDate();
        dates[3] = colorFavesBean.getFave4().getDate();
        dates[4] = colorFavesBean.getFave5().getDate();

        long min = Long.MAX_VALUE;
        int pos = 0;
        for (int i = 0; i < dates.length; i++)
        {
            if (dates[i] < min)
            {
                min = dates[i];
                pos = i;
            }
        }

        return pos;
    }


    public static void putFaveColor(int color)
    {
        int pos = findFavPos();
        ColorFaveBean colorFaveBean = new ColorFaveBean(color + "", pos, System.currentTimeMillis());
        ColorFavesBean colorFavesBean = getColorsFromSharedPref();
        if (colorFavesBean == null)
            colorFavesBean = new ColorFavesBean();
        switch (pos)
        {
            case 0:
                colorFavesBean.setFave1(colorFaveBean);
                break;
            case 1:
                colorFavesBean.setFave2(colorFaveBean);
                break;
            case 2:
                colorFavesBean.setFave3(colorFaveBean);
                break;
            case 3:
                colorFavesBean.setFave4(colorFaveBean);
                break;
            case 4:
                colorFavesBean.setFave5(colorFaveBean);
                break;
        }
        saveColorsToSharedPref(colorFavesBean);

    }

    public static class WSParameter {
        public String key;
        public Object value;

        public WSParameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public static String getURLWithParams(String ws, ArrayList<WSParameter> parameters) {
        if (parameters == null)
            return ws;
        ws += "?";
        for (int i = 0; i < parameters.size() - 1; i++) {
            WSParameter wsParameter = parameters.get(i);
            ws += wsParameter.key + "=" + wsParameter.value + "&";
        }
        WSParameter wsParameter = parameters.get(parameters.size() - 1);
        ws += wsParameter.key + "=" + wsParameter.value;
        Log.e("Tag", "ws = " + ws);
        return ws;
    }

    public static void changeTabsFont(TabLayout tabLayout) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(NiligoPrismApplication.getInstance().getTypeface());
                }
            }
        }
    }

    public static ScreenSize getScreenSize() {
        Display display = ((WindowManager) NiligoPrismApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            return new ScreenSize(size.x, size.y);
        } else {
            return new ScreenSize(display.getWidth(), display.getHeight());
        }
    }

    public static int getActionbarHeight(AppCompatActivity activity) {
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.actionBarSize });
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return mActionBarSize;
    }

    public static int getStatusbarHeight(AppCompatActivity activity)
    {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getGroupBackground(GroupType group_type) {

        switch (group_type)
        {
            case BATHING_ROOM:
                return R.drawable.bg_bathing_room;
            case BEDROOM:
                return R.drawable.bg_bedroom;
            case CUBICLE:
                return R.drawable.bg_cubicle;
            case DINING_ROOM:
                return R.drawable.bg_dinning_room;
            case GREAT_HALL:
                return R.drawable.bg_great_hall;
            case KITCHEN:
                return R.drawable.bg_kitchen;
            case LIVING_ROOM:
                return R.drawable.bg_living_room;
            case MASTER_BEDROOM:
                return R.drawable.bg_master_bedroom;
            case STUDY_ROOM:
                return R.drawable.bg_study_room;
            case WORKSPACE:
                return R.drawable.bg_workspace;
            default:
                return R.drawable.bg_bedroom;
        }
    }

    public static int getGroupIcon(GroupType group_type) {

        switch (group_type)
        {
            case BATHING_ROOM:
                return R.drawable.type_bathing_room;
            case BEDROOM:
                return R.drawable.type_bed_room;
            case CUBICLE:
                return R.drawable.type_cubicle;
            case DINING_ROOM:
                return R.drawable.type_dinning_room;
            case GREAT_HALL:
                return R.drawable.type_great_hall;
            case KITCHEN:
                return R.drawable.type_kitchen;
            case LIVING_ROOM:
                return R.drawable.type_living_room;
            case MASTER_BEDROOM:
                return R.drawable.type_master_bedroom;
            case STUDY_ROOM:
                return R.drawable.type_study_room;
            case WORKSPACE:
                return R.drawable.type_workspace;
            default:
                return R.drawable.type_bed_room;
        }
    }

    public static boolean isConnected() {
        boolean status=false;
        try{
            ConnectivityManager cm = NiligoPrismApplication.getInstance().getConnectivityManager();
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public static class ScreenSize {
        public int width, height;

        public ScreenSize(int x, int y) {
            this.width = x;
            this.height = y;
        }
    }

    public static void handleError(int error, Activity context)
    {
        switch (error)
        {
            case 101:
                Utils.showToast(context, R.string.error_invalid_credentials, Toast.LENGTH_LONG);
                break;
            case 104:
                Utils.showToast(context, R.string.error_invalid_email, Toast.LENGTH_LONG);
                break;
            case 105:
                Utils.showToast(context, R.string.error_weak_password, Toast.LENGTH_LONG);
                break;
            case 102:
                Utils.showToast(context, R.string.error_invalid_token, Toast.LENGTH_LONG);
                break;
        }
    }
}

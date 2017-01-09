package com.niligo.prism.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.niligo.prism.ServerCoordinator;
import com.niligo.prism.callback.GetColorCallback;
import com.niligo.prism.callback.GetPowerCallback;

/**
 * Created by mahdi on 8/2/16.
 */
public class BulbBean implements Parcelable {

    private int id;
    private String name;
    private String description;
    private String iid;
    private String uuid;
    private String firmware;
    private String user;
    private String pass;
    private int uptime;
    private int heap;
    private String color;
    private int fade;
    private boolean power;
    private String ssid;
    private String network_password;
    private boolean dhcp;
    private String ip;
    private String gw;
    private String netmask;
    private String mac;
    private boolean isSelected;
    private String sta_ssid;
    private int group_id;

    public BulbBean(int id) {
        this.id = id;
    }

    public BulbBean(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public BulbBean(String color, boolean power) {
        this.color = color;
        this.power = power;
    }

    public BulbBean(int id, String ssid, boolean isSelected) {
        this.id = id;
        this.ssid = ssid;
        this.isSelected = isSelected;
    }

    public BulbBean(int id, String name, String description, String iid, String uuid,
                    String firmware, String user, String pass, int uptime, int heap,
                    String color, int fade, boolean power, String ssid, String network_password,
                    boolean dhcp, String ip, String gw, String netmask, String mac, boolean isSelected,
                    String sta_ssid, int group_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iid = iid;
        this.uuid = uuid;
        this.firmware = firmware;
        this.user = user;
        this.pass = pass;
        this.uptime = uptime;
        this.heap = heap;
        this.color = color;
        this.fade = fade;
        this.power = power;
        this.ssid = ssid;
        this.network_password = network_password;
        this.dhcp = dhcp;
        this.ip = ip;
        this.gw = gw;
        this.netmask = netmask;
        this.mac = mac;
        this.isSelected = isSelected;
        this.sta_ssid = sta_ssid;
        this.group_id = group_id;
    }

    public String getWS()
    {
        return "http://" + ip + "/api/";
    }

    public String getSta_ssid() {
        return sta_ssid;
    }

    public void setSta_ssid(String sta_ssid) {
        this.sta_ssid = sta_ssid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getUptime() {
        return uptime;
    }

    public void setUptime(int uptime) {
        this.uptime = uptime;
    }

    public int getHeap() {
        return heap;
    }

    public void setHeap(int heap) {
        this.heap = heap;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getFade() {
        return fade;
    }

    public void setFade(int fade) {
        this.fade = fade;
    }

    /*public void isPower(final GetPowerCallback getPowerCallback) {

        ServerCoordinator.getInstance()
                .setBulbCredentials(
                    getUser(),
                    getPass(),
                    getWS()
                ).getColor(new GetColorCallback() {

            @Override
            public void getColorSuccess(ColorBean colorBean) {

                if (colorBean == null)
                    power = false;
                else
                    power = colorBean.getPower() == 1;

                getPowerCallback.getPowerSuccess(power);
            }

            @Override
            public void getColorFailure(int error_code) {

                getPowerCallback.getPowerFailure(500);
            }
        });
    }*/

    public boolean isPower() {

        ColorBean colorBean = ServerCoordinator.getInstance()
                .setBulbCredentials(
                        getUser(),
                        getPass(),
                        getWS()
                ).getColor();

        if (colorBean == null)
            power = false;
        else
            power = colorBean.getPower() == 1;

        return power;
    }


    public void setPower(boolean power) {
        this.power = power;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getNetwork_password() {
        return network_password;
    }

    public void setNetwork_password(String network_password) {
        this.network_password = network_password;
    }

    public boolean isDhcp() {
        return dhcp;
    }

    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGw() {
        return gw;
    }

    public void setGw(String gw) {
        this.gw = gw;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.iid);
        dest.writeString(this.uuid);
        dest.writeString(this.firmware);
        dest.writeString(this.user);
        dest.writeString(this.pass);
        dest.writeInt(this.uptime);
        dest.writeInt(this.heap);
        dest.writeString(this.color);
        dest.writeInt(this.fade);
        dest.writeByte(this.power ? (byte) 1 : (byte) 0);
        dest.writeString(this.ssid);
        dest.writeString(this.network_password);
        dest.writeByte(this.dhcp ? (byte) 1 : (byte) 0);
        dest.writeString(this.ip);
        dest.writeString(this.gw);
        dest.writeString(this.netmask);
        dest.writeString(this.mac);
        dest.writeString(this.sta_ssid);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.group_id);
    }

    protected BulbBean(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.iid = in.readString();
        this.uuid = in.readString();
        this.firmware = in.readString();
        this.user = in.readString();
        this.pass = in.readString();
        this.uptime = in.readInt();
        this.heap = in.readInt();
        this.color = in.readString();
        this.fade = in.readInt();
        this.power = in.readByte() != 0;
        this.ssid = in.readString();
        this.network_password = in.readString();
        this.dhcp = in.readByte() != 0;
        this.ip = in.readString();
        this.gw = in.readString();
        this.netmask = in.readString();
        this.mac = in.readString();
        this.sta_ssid = in.readString();
        this.isSelected = in.readByte() != 0;
        this.group_id = in.readInt();

    }

    public static final Creator<BulbBean> CREATOR = new Creator<BulbBean>() {
        @Override
        public BulbBean createFromParcel(Parcel source) {
            return new BulbBean(source);
        }

        @Override
        public BulbBean[] newArray(int size) {
            return new BulbBean[size];
        }
    };
}


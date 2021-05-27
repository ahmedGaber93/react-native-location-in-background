package com.reactnativelocationinbackground;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Config {


    private String notificationTitle = "Background Tracking";
    private String notificationText = "Background Tracking enabled";
    private Boolean showLatLngInNotificationForTest = false;
    private Integer interval = 20000; //milliseconds
    private Integer fastestInterval = 10000; //milliseconds
    private String url = null;
    private JSONObject httpHeaders = new JSONObject();
    private JSONObject extraPostData = new JSONObject();




    public Config(){

    }




    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public void setShowLatLngInNotificationForTest(Boolean showLatLngInNotificationForTest) {
        this.showLatLngInNotificationForTest = showLatLngInNotificationForTest;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setFastestInterval(Integer fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttpHeaders(JSONObject httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public void setExtraPostData(JSONObject extraPostData) {
        this.extraPostData = extraPostData;
    }









    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public Boolean getShowLatLngInNotificationForTest() {
        return showLatLngInNotificationForTest;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getFastestInterval() {
        return fastestInterval;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHttpHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> keys = httpHeaders.keys();
        try {
            while(keys.hasNext()) {
                String key = keys.next();
                map.put(key, httpHeaders.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public JSONObject getExtraPostData() {
        return extraPostData;
    }

    public JSONObject getData(Location lastLocation) {
        try {
            extraPostData.put("altitude", lastLocation.getAltitude());
            extraPostData.put("latitude", lastLocation.getLatitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extraPostData;
    }









}
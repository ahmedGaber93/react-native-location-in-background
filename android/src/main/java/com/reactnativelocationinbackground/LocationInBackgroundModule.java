
package com.reactnativelocationinbackground;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import org.json.JSONException;
import org.json.JSONObject;



@ReactModule(name = LocationInBackgroundModule.NAME)
public class LocationInBackgroundModule extends ReactContextBaseJavaModule {


    public static final String NAME = "LocationInBackground";

    private static final String TAG = com.reactnativelocationinbackground.LocationInBackgroundModule.class.getSimpleName();
    private static final String ERROR_INVALID_CONFIG = "ERROR_INVALID_CONFIG";


    Config mConfig;

    private LocationUpdatesService mService = null;

    //https://developer.android.com/guide/components/bound-services
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };




    public LocationInBackgroundModule(ReactApplicationContext reactContext) {

        super(reactContext);

        //https://developer.android.com/guide/components/bound-services
        reactContext.bindService(
            new Intent(getReactApplicationContext(), LocationUpdatesService.class),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        );

    }





    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void startTracking() {
        mService.startServiceForeground();
    }

    @ReactMethod
    public void stopTracking() {
        mService.stopServiceForeground();
    }



    @ReactMethod
    public boolean isTracking() {
        return mService.serviceIsRunningInForeground(getReactApplicationContext());
    }



    @ReactMethod
    public void configure(ReadableMap config, Promise promise) {

        mConfig = new Config();

        if (config == null) {
            promise.reject(ERROR_INVALID_CONFIG, "LocationInBackground configure error: missing config object");
            return;
        }

        if (config.hasKey("notificationTitle") && !config.isNull("notificationTitle")){
            mConfig.setNotificationTitle(config.getString("notificationTitle"));
        }

        if (config.hasKey("notificationText") && !config.isNull("notificationText")) {
            mConfig.setNotificationText(config.getString("notificationText"));
        }

        if (config.hasKey("showLatLngInNotificationForTest") && !config.isNull("showLatLngInNotificationForTest")) {
            mConfig.setShowLatLngInNotificationForTest(config.getBoolean("showLatLngInNotificationForTest"));
        }

        if (config.hasKey("interval") && !config.isNull("interval")) {
            mConfig.setInterval(config.getInt("interval"));
        }

        if (config.hasKey("fastestInterval") && !config.isNull("fastestInterval")) {
            mConfig.setFastestInterval(config.getInt("fastestInterval"));
        }

        if (config.hasKey("url") && !config.isNull("url")) {
            mConfig.setUrl(config.getString("url"));
        }


        if (config.hasKey("httpHeaders") && !config.isNull("httpHeaders")) {
            try {
                JSONObject jsonObject = new JSONObject(config.getString("httpHeaders"));
                mConfig.setHttpHeaders(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                promise.reject(ERROR_INVALID_CONFIG , "LocationInBackground configure error: httpHeaders type not valid");
            }
        }


        if (config.hasKey("extraPostData") && !config.isNull("extraPostData")) {
            try {
                JSONObject jsonObject = new JSONObject(config.getString("extraPostData"));

                Log.i(TAG, "config extraPostData : " + jsonObject.toString());
                mConfig.setExtraPostData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                promise.reject(ERROR_INVALID_CONFIG , "LocationInBackground configure error: extraPostData type not valid");
            }
        }



        if (config.hasKey("paramsNames") && !config.isNull("paramsNames")) {
            try {
                JSONObject jsonObject = new JSONObject(config.getString("paramsNames"));

                Log.i(TAG, "config paramsNames : " + jsonObject.toString());
                mConfig.setParamsNames(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                promise.reject(ERROR_INVALID_CONFIG , "LocationInBackground configure error: paramsNames type not valid");
            }
        }


        promise.resolve("LocationInBackground configure successfully");

        mService.setConfig(mConfig);

    }















}

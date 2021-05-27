package com.reactnativelocationinbackground;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;


public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.reactnativelocationinbackground";

    private static final String TAG = com.reactnativelocationinbackground.LocationUpdatesService.class.getSimpleName();

    private static final String CHANNEL_ID = "location_in_foreground_channel_1";
    private static final String CHANNEL_NAME = "location_in_foreground_channel_1_name";

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    private static final int NOTIFICATION_ID = 123456789;

    private NotificationManager mNotificationManager;


    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;


    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;



    /**
     * current location.
     */
    private Location mLocation;

    private Config mConfig = new Config();

    public LocationUpdatesService() {

    }




    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(mConfig.getShowLatLngInNotificationForTest()) {
                    onNewLocation(locationResult.getLastLocation());
                }
                if (mConfig.getUrl() != null){
                    try {
                        HttpPostService.postJSON(
                            mConfig.getUrl(),
                            mConfig.getData(locationResult.getLastLocation()),
                            mConfig.getHttpHeaders()
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        createLocationRequest();
        getLastLocation();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

        // When user decided to stopForeground from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
            stopForeground(true);
        }

        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        if (PreferenceManagerUtils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }


    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        PreferenceManagerUtils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), com.reactnativelocationinbackground.LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            PreferenceManagerUtils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            PreferenceManagerUtils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            PreferenceManagerUtils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    private Notification getNotification() {
        Intent intent = new Intent(this, com.reactnativelocationinbackground.LocationUpdatesService.class);

        CharSequence text =  mLocation == null
            ? "Unknown location"
            : "(" + mLocation.getLatitude() + ", " + mLocation.getLongitude() + ")";


        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop",
                        servicePendingIntent)
                .setContentText(mConfig.getShowLatLngInNotificationForTest() ? text : mConfig.getNotificationText())
                .setContentTitle(mConfig.getShowLatLngInNotificationForTest() ? ("Location Updated: " + DateFormat.getDateTimeInstance().format(new Date())) : mConfig.getNotificationTitle())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(getResourceIdForResourceName(getApplicationContext(), "tracking"))
                .setTicker(mConfig.getShowLatLngInNotificationForTest() ? text : mConfig.getNotificationText())
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis());

        if (getMainActivityClass(getApplicationContext()) != null){
            // The PendingIntent to launch activity.
            PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getMainActivityClass(getApplicationContext())), 0);

            builder.setContentIntent(activityPendingIntent);

        }

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mConfig.getInterval());
        mLocationRequest.setFastestInterval(mConfig.getFastestInterval());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        com.reactnativelocationinbackground.LocationUpdatesService getService() {
            return com.reactnativelocationinbackground.LocationUpdatesService.this;
        }
    }




    public void startServiceForeground() {
        startForeground(NOTIFICATION_ID, getNotification());
        requestLocationUpdates();
    }


    public void stopServiceForeground() {
        removeLocationUpdates();
        stopForeground(true);
    }


    public void setConfig(Config config) {
        this.mConfig = config;
    }

    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }
        return resourceId;
    }



    private Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e("NotificationError", "Failed to get launch intent or component");
            return null;
        }
        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e("NotificationError", "Failed to get main activity class");
            return null;
        }
    }



    private boolean isNotificationVisible() {
        Intent intent = new Intent(this, getMainActivityClass(this));
        PendingIntent test = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }



    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}

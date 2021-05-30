# Android setup
##### 1) - android/build.gradle
update `build.gradle` under `android folder`.
```java
buildscript {
    ext {
        ....
        //add this two line
        playServicesVersion = "17.0.0" // or find latest version
        androidXCore = "1.0.2"
    }
    ....
}
```
##### 2) - add permissions
Add permission in your `AndroidManifest.xml`.
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

##### 3) - define ForegroundService
Add `service` in your `AndroidManifest.xml` inside `application` tag.
```xml

<application
  ...>
  
  <!-- Foreground services in Q+ require type. -->
  <service
    android:name="com.reactnativelocationinbackground.LocationUpdatesService"
    android:enabled="true"
    android:exported="true"
    android:foregroundServiceType="location" />

</application>

```






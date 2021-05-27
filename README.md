# react-native-location-in-background

tracking user location in background

## Installation

```sh
npm install react-native-location-in-background
```

#### Android setup
##### 1) - Permissions
Add permission in your `AndroidManifest.xml`.
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

##### 2) - foregroundService
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

## Usage

```js
import * as React from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import LocationInBackground from 'react-native-location-in-background';


export default function App() {

    
    const configure = () => {
        LocationInBackground.configure(
            {
                extraPostData : {key1 : "value1", key2 : "value2"},
                notificationText : "tracking enable",
                notificationTitle : "tracking",
                interval : 30000,
                fastestInterval : 20000,
                url : "", //your url here
                showLatLngInNotificationForTest : false
            }
        )
    };


    const startTracking = () => {
        LocationInBackground.startTracking();
    };


    const stopTracking = () => {
        LocationInBackground.stopTracking();
    };

    return (
        <View style={styles.container}>
            <TouchableOpacity style={styles.btn} onPress={configure}><Text>configure</Text></TouchableOpacity>
            <TouchableOpacity style={styles.btn} onPress={startTracking}><Text>start tracking</Text></TouchableOpacity>
            <TouchableOpacity style={styles.btn} onPress={stopTracking}><Text>stop tracking</Text></TouchableOpacity>
        </View>
    );



}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    box: {
        width: 60,
        height: 60,
        marginVertical: 20,
    },
    btn : {
        paddingVertical : 5,
        marginVertical : 5,
    }
});
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

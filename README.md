# react-native-location-in-background

tracking user location all time in android && ios platform in background && foreground without killing.

## how it work?
##### 1) - android
* use `ForegroundService` with notification.<br>
* the location update every n second.

##### 2) - ios
* use `CLLocationManager` with Significant Location Updates `startMonitoringSignificantLocationChanges`.<br>
* the location updates based on significant location changes [read here](https://developer.apple.com/documentation/corelocation/cllocationmanager/1423531-startmonitoringsignificantlocati?language=objc).
## Installation

```sh
npm install react-native-location-in-background
```

* [Android setup.](https://github.com/ahmedGaber93/react-native-location-in-background/tree/master/doc/ANDROID_SETUP.md)
* [Ios setup.](https://github.com/ahmedGaber93/react-native-location-in-background/tree/master/doc/IOS_SETUP.md)


## Usage

```js

import * as React from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert, Platform } from 'react-native';
import LocationInBackground from 'react-native-location-in-background';



export default function App() {


    React.useEffect(() => {
        LocationInBackground.configure(
            {
                extraPostData : {key1 : "value1", key2 : "value2"},
                httpHeaders : {"header1" : "header value1", "header2" : "header value2"},
                notificationText : "tracking is running",
                notificationTitle : "tracking your location",
                interval : 30000,
                fastestInterval : 20000,
                url : "", //your url here
            }
        )
    }, [])


    const startTracking = () => {
        LocationInBackground.startTracking();
    };


    const iosCheckPermission = () => {
        LocationInBackground.iosCheckPermission()
        .then((x) => {
            // @ts-ignore
            alert(x);
        });
    };


    const iosOpenSettings = () => {
        Alert.alert(
            "enable  always location permission",
            "to tracking user in background you must set location permission to always.",
            [
              {
                text: "Cancel",
                onPress: () => console.log("Cancel Pressed"),
                style: "cancel",
              },
              { text: "OK", onPress: () => LocationInBackground.iosOpenSettings() }
            ]
          );

    };
    const stopTracking = () => {
        LocationInBackground.stopTracking();
    };

    return (
        <View style={styles.container}>
            <TouchableOpacity style={styles.btn} onPress={startTracking}><Text>start tracking</Text></TouchableOpacity>
            <Text style={styles.heading}>ask for permission and then start Tracking.</Text>

            <View style={styles.line} />

            <TouchableOpacity style={styles.btn} onPress={stopTracking}><Text>stop tracking</Text></TouchableOpacity>


            {Platform.OS === "ios" &&
            <>
            <View style={styles.line} />
            <Text style={styles.heading}>in ios if the user don't accept always permission you can open "iphone settings app" and force user to accept it</Text>

            <TouchableOpacity style={styles.btn} onPress={iosCheckPermission}><Text>ios check Permission</Text></TouchableOpacity>
            <TouchableOpacity style={styles.btn} onPress={iosOpenSettings}><Text>ios open Settings to force Permission</Text></TouchableOpacity>

            </>
            }
        </View>
    );



}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    line: {
        height : 1,
        width : "90%",
        marginVertical: 20,
        backgroundColor : "#999"
    },
    btn : {
        paddingVertical : 5,
        marginVertical : 5,
        borderWidth : 1,
        borderColor : "#0099cc",
        paddingHorizontal : 15,
    },
    btn2 : {
        paddingVertical : 5,
        marginVertical : 5,
        borderWidth : 1,
        borderColor : "#999",
        paddingHorizontal : 15,
        width : "90%",
    },
    heading : {
        padding : 15,
        textAlign : "center",
    }
});

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

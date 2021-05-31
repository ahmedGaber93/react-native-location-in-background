import * as React from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Alert, Platform } from 'react-native';
import LocationInBackground from 'react-native-location-in-background';



export default function App() {


    React.useEffect(() => {
        LocationInBackground.configure(
            {
                extraPostData : {
                    key1 : "value1",
                    key2 : "value2"
                },
                paramsNames : {
                    latitude : "lat",
                    longitude : "lng",
                    time : "lastSync"
                },
                httpHeaders : {"header1" : "header value1", "header2" : "header value2"},
                notificationText : "tracking is running",
                notificationTitle : "tracking your location",
                interval : 30000,
                fastestInterval : 20000,
                url : "", //your url here
            }
        ).then((results : String) => {
            console.log("LocationInBackground.configure", results);
        }).catch(e => console.error("LocationInBackground.configure error :", e.message));
    }, []);


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

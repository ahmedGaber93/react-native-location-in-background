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

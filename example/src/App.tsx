import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import LocationInBackground from 'react-native-location-in-background';







export default function App() {




    const press1 = () => {
        LocationInBackground.configure(
            {
                extraPostData : {key : "value", key2 : {xx : 3}},
                notificationText : "tracking enable",
                notificationTitle : "tracking".toUpperCase(),
                interval : 10000,
                showLatLngInNotificationForTest : false
            }
        )
    };


    const press2 = () => {
        LocationInBackground.startTracking();
    };


    const press3 = () => {
        LocationInBackground.stopTracking();
    };

    return (
        <View style={styles.container}>
            <TouchableOpacity style={styles.btn} onPress={press1}><Text>configure</Text></TouchableOpacity>
            <TouchableOpacity style={styles.btn} onPress={press2}><Text>start tracking</Text></TouchableOpacity>
            <TouchableOpacity style={styles.btn} onPress={press3}><Text>stop tracking</Text></TouchableOpacity>
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
    },

});

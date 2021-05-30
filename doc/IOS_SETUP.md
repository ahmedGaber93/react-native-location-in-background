# Ios setup

##### 1) - update your `Info.plist`.

```xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>tracking user location</string>

<key>NSLocationAlwaysUsageDescription</key>
<string>tracking user location</string>

<key>NSLocationWhenInUseUsageDescription</key>
<string>tracking user location</string>
```

##### 2) - enabled location updates.

![img1](https://github.com/ahmedGaber93/react-native-location-in-background/tree/master/doc/img1.gif)



##### 3) - edit `AppDelegate.m`.
```c#

 //add this line
#import <LocationService.h>

...

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

  ....
  
  //add this 3 lines
  if (launchOptions[UIApplicationLaunchOptionsLocationKey]) {
    [[LocationService sharedLocationService] handleNewLocation];
  }
  
  ....
  
  return YES;
}

```






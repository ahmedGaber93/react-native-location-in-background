# Ios setup


##### 1) - pod install.
inside ios folder run
```sh
pod install
```
##### 2) - update your `Info.plist`.

```xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>tracking user location</string>

<key>NSLocationAlwaysUsageDescription</key>
<string>tracking user location</string>

<key>NSLocationWhenInUseUsageDescription</key>
<string>tracking user location</string>
```

##### 3) - enabled location updates.

![img1](https://raw.githubusercontent.com/ahmedGaber93/react-native-location-in-background/master/doc/img1.gif)



##### 4) - edit `AppDelegate.m`.
```c+

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






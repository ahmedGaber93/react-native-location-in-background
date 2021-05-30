//
//  LocationService.m
//  LocationInBackground
//
//  Created by user179267 on 5/27/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <LocationService.h>
#import <CoreLocation/CoreLocation.h>


@implementation LocationService;


static LocationService *_sharedLocationService = nil;
CLLocationManager *_locationManager;

void (^_resolve)(NSNumber *status);
void (^_reject)(NSError *error);
BOOL isTrackingStart = NO;


+(LocationService*)sharedLocationService{
    @synchronized([LocationService class]) {
        if (!_sharedLocationService){
            _sharedLocationService = [[self alloc] init];
        }
        return _sharedLocationService;
    }
    return nil;
}

+(id)alloc {
    @synchronized([LocationService class])
    {
        NSAssert(_sharedLocationService == nil, @"Singleton already initialized.");
        _sharedLocationService = [super alloc];
        return _sharedLocationService;
    }
    return nil;
}

-(id)init {
    self = [super init];
    if (self != nil) {
        _locationManager = [[CLLocationManager alloc] init];
        _locationManager.delegate = self;
        _locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation;
        _locationManager.allowsBackgroundLocationUpdates = true;
    }   return self;
}


- (void)requestPermission:(void (^ _Nonnull)(NSNumber *))resolve
                 rejecter:(void (^ _Nonnull)(NSError * _Nonnull))reject{
    _reject = reject;
    _resolve = resolve;

    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    switch (status) {

        case kCLAuthorizationStatusDenied:
            _resolve([NSNumber numberWithInt:1]);
            break;
        case kCLAuthorizationStatusRestricted:
            _resolve([NSNumber numberWithInt:2]);
            break;
        case kCLAuthorizationStatusNotDetermined:
            [_locationManager requestWhenInUseAuthorization];
            break;
        case kCLAuthorizationStatusAuthorizedAlways:
            _resolve([NSNumber numberWithInt:4]);
            break;
        case kCLAuthorizationStatusAuthorizedWhenInUse:
            _resolve([NSNumber numberWithInt:5]);
            break;
    }
}


- (void)checkPermission:(void (^ _Nonnull)(NSNumber *))resolve
                 rejecter:(void (^ _Nonnull)(NSError * _Nonnull))reject{

    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    switch (status) {

        case kCLAuthorizationStatusDenied:
            resolve([NSNumber numberWithInt:1]);
            break;
        case kCLAuthorizationStatusRestricted:
            resolve([NSNumber numberWithInt:2]);
            break;
        case kCLAuthorizationStatusNotDetermined:
            resolve([NSNumber numberWithInt:3]);
            break;
        case kCLAuthorizationStatusAuthorizedAlways:
            resolve([NSNumber numberWithInt:4]);
            break;
        case kCLAuthorizationStatusAuthorizedWhenInUse:
            resolve([NSNumber numberWithInt:5]);
            break;
    }
}







- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations{
    CLLocation *location = locations.lastObject;
    [self sendToServer:location];
}




- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {


    if (status == kCLAuthorizationStatusAuthorizedWhenInUse) {
        if (_locationManager) {
            [_locationManager requestAlwaysAuthorization];
            if(isTrackingStart){
                [_locationManager startMonitoringSignificantLocationChanges];
            }
        }
    }
    else if (status == kCLAuthorizationStatusAuthorizedAlways) {
        if (_resolve) {
            _resolve([NSNumber numberWithInt:4]);
        }
    }
    else if (status == kCLAuthorizationStatusDenied) {
        if (_resolve) {
            _resolve([NSNumber numberWithInt:1]);
        }
    }
    else if (status == kCLAuthorizationStatusRestricted) {
        if (_resolve) {
            _resolve([NSNumber numberWithInt:2]);
        }
    }


}


-(void)setConfig:(NSDictionary *)config{

    [[NSUserDefaults standardUserDefaults] setObject:config forKey:@"LocationInBackgroundConfigKey"];
}






-(void)startTracking{
    isTrackingStart = YES;
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    if(status == kCLAuthorizationStatusAuthorizedAlways){
        [_locationManager startMonitoringSignificantLocationChanges];
    }
    else if(status == kCLAuthorizationStatusAuthorizedWhenInUse){
        [_locationManager startMonitoringSignificantLocationChanges];
    }
    else if(status == kCLAuthorizationStatusNotDetermined){
        [_locationManager requestWhenInUseAuthorization];
    }
}



-(void)stopTracking{
    isTrackingStart = NO;
    [_locationManager stopMonitoringSignificantLocationChanges];
}



-(void)handleNewLocation{
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    if(status == kCLAuthorizationStatusAuthorizedAlways){
        [_locationManager startMonitoringSignificantLocationChanges];
    }
}




-(void)sendToServer:(CLLocation *) location{
    NSDictionary * config = [[NSUserDefaults standardUserDefaults] dictionaryForKey:@"LocationInBackgroundConfigKey"];


    NSString * latitude = [NSString stringWithFormat:@"%.6f", location.coordinate.latitude];
    NSString * longitude = [NSString stringWithFormat:@"%.6f", location.coordinate.longitude];

    NSString * url = [config objectForKey:@"url"];
    NSDictionary * extraPostData = [config objectForKey:@"extraPostData"];
    NSDictionary * httpHeaders = [config objectForKey:@"httpHeaders"];
    NSDictionary * paramsNames = [config objectForKey:@"paramsNames"];


    NSDictionary *dic = @{
        @"latitude" : latitude,
        @"longitude" : longitude,
    };

    NSMutableDictionary *Dic2 =  [[NSMutableDictionary alloc] init];
    [Dic2 setObject:latitude forKey:paramsNames[@"latitude"]];
    [Dic2 setObject:longitude forKey:paramsNames[@"longitude"]];

    [Dic2 addEntriesFromDictionary:extraPostData];


    NSError *error = nil;

    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[Dic2 copy]
         options:NSJSONWritingPrettyPrinted error:&error];



    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:url]];

    [urlRequest setHTTPBody:jsonData];
    [urlRequest setHTTPMethod:@"POST"];

    for (NSString* key in httpHeaders) {
        [urlRequest setValue:httpHeaders[key] forHTTPHeaderField:key];
    }




    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:urlRequest completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if(httpResponse.statusCode == 200)
        {
            NSError *parseError = nil;
            NSDictionary *responseDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&parseError];
            NSLog(@"The response is - %@",responseDictionary);
        }
        else
        {
            NSLog(@"Error");
        }
    }];
    [dataTask resume];



}















@end





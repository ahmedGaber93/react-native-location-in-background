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
        _locationManager.distanceFilter = 50;
        [self checkLocationAccess];
        [self startTracking];
    }   return self;
}





- (void)checkLocationAccess {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    switch (status) {

    // custom methods after each case

        case kCLAuthorizationStatusDenied:

            break;
        case kCLAuthorizationStatusRestricted:

            break;
        case kCLAuthorizationStatusNotDetermined:
            [_locationManager requestWhenInUseAuthorization];
            break;
        case kCLAuthorizationStatusAuthorizedAlways:
            break;
        case kCLAuthorizationStatusAuthorizedWhenInUse:
            [_locationManager requestAlwaysAuthorization];
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
        [_locationManager requestAlwaysAuthorization];
        NSLog(@"allowed"); // allowed
    }
    else if (status == kCLAuthorizationStatusDenied) {
        NSLog(@"denied"); // denied
    }
}


-(void)setConfig:(NSDictionary *)config{
    
    [[NSUserDefaults standardUserDefaults] setObject:config forKey:@"LocationInBackgroundConfigKey"];
    //[LocationService sharedLocationService];
    NSLog(@"%@",[config objectForKey:@"extraPostData"]);
    
    
}


-(void)startTracking{
    [_locationManager startMonitoringSignificantLocationChanges];
    
}



-(void)stotTracking{
    [_locationManager stopMonitoringSignificantLocationChanges];

}

-(void)sendToServer:(CLLocation *) location{
    NSDictionary * config = [[NSUserDefaults standardUserDefaults] dictionaryForKey:@"LocationInBackgroundConfigKey"];
    
    
    NSString * latitude = [NSString stringWithFormat:@"%.6f", location.coordinate.latitude];
    NSString * longitude = [NSString stringWithFormat:@"%.6f", location.coordinate.longitude];

    NSString * url = [config objectForKey:@"url"];
    NSDictionary * extraPostData = [config objectForKey:@"extraPostData"];
    NSDictionary * httpHeaders = [config objectForKey:@"httpHeaders"];


    NSDictionary *dic = @{
        @"latitude" : latitude,
        @"longitude" : longitude,
        
    };
    
    NSMutableDictionary * Dic2 = [dic mutableCopy];
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





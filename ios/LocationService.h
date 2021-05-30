//
//  LocationService.h
//  LocationInBackground
//
//  Created by user179267 on 5/27/21.
//  Copyright © 2021 Facebook. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>


#ifndef LocationService_h
#define LocationService_h


@interface LocationService : NSObject <CLLocationManagerDelegate>

+ (LocationService *)sharedLocationService;
- (void)setConfig:(NSDictionary *)config;
- (void)requestPermission:(void (^ _Nonnull)(NSNumber *))resolve
                 rejecter:(void (^ _Nonnull)(NSError * _Nonnull))reject;

- (void)checkPermission:(void (^ _Nonnull)(NSNumber *))resolve
                 rejecter:(void (^ _Nonnull)(NSError * _Nonnull))reject;

- (void)forceOpenSetting;
- (void)startTracking;
- (void)stopTracking;
- (void)handleNewLocation;
@end

#endif /* LocationService_h */

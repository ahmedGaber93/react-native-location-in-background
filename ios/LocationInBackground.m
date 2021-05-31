#import "LocationInBackground.h"
#import "LocationService.h"

@implementation LocationInBackground

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}


RCT_REMAP_METHOD(multiply,
                 multiplyWithA:(nonnull NSNumber*)a withB:(nonnull NSNumber*)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  NSNumber *result = @([a floatValue] * [b floatValue]);

  resolve(result);
}




RCT_REMAP_METHOD(configure,
                 configureWithConfig:(nonnull NSDictionary*)config
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[LocationService sharedLocationService] setConfig:config setConfigWithResolve:^(NSString *status) {
        resolve(status);
      } rejecter:^(NSError *error) {
        
          
      }];
 
}






RCT_REMAP_METHOD(startTracking,
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[LocationService sharedLocationService] startTracking];
 
}



RCT_REMAP_METHOD(stopTracking,
                 stopTrackingWithResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[LocationService sharedLocationService] stopTracking];
 
}





RCT_REMAP_METHOD(requestPermission,
                 requestWithResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[LocationService sharedLocationService] requestPermission:^(NSNumber *status) {
        resolve(status);
      } rejecter:^(NSError *error) {
        
          
      }];
 
}



RCT_REMAP_METHOD(iosCheckPermission,
                 checkPermissionWithResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[LocationService sharedLocationService] checkPermission:^(NSNumber *status) {
        resolve(status);
      } rejecter:^(NSError *error) {
        
          
      }];
 
}





RCT_REMAP_METHOD(iosOpenSettings,
                 openWithResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
 
}









@end

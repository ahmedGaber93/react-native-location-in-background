import { NativeModules, Platform } from 'react-native';


enum IosStatus {
  Denied = 1,
  Restricted,
  NotDetermined,
  Always,
  WhenInUse,
}


type ConfigType = {
  notificationTitle? : string;
  notificationText? : string;
  showLatLngInNotificationForTest? : boolean;
  interval? : number;
  fastestInterval? : number;
  url? : string;
  httpHeaders? : any;
  extraPostData? : any;
};


type LocationInBackgroundType = {
  startTracking(): void;
  requestPermission(): Promise<any>;
  iosCheckPermission(): Promise<IosStatus>;
  iosOpenSettings(): void;
  stopTracking(): void;
  configure(config : ConfigType) : Promise<any>;
};

const { LocationInBackground } = NativeModules;


const configure = (config : ConfigType) => {
  config.httpHeaders = config.httpHeaders || {};
  config.extraPostData = config.extraPostData || {};
  config.httpHeaders =  Platform.OS === "ios" ? config.httpHeaders : JSON.stringify(config.httpHeaders);
  config.extraPostData =  Platform.OS === "ios" ? config.extraPostData : JSON.stringify(config.extraPostData);

  return LocationInBackground.configure(
      config
  )
};


const iosCheckPermission = () => {
  return Platform.OS === "ios" ? LocationInBackground.iosCheckPermission() : null;
};

const iosOpenSettings = () => {
  return Platform.OS === "ios" ? LocationInBackground.iosOpenSettings() : null;
};

const exportLocationInBackground = {
    ...LocationInBackground,
    configure,
    iosCheckPermission,
    iosOpenSettings,
};

export default  exportLocationInBackground  as LocationInBackgroundType;

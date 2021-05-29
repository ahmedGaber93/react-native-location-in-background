import { NativeModules, Platform } from 'react-native';


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

const exportLocationInBackground = {
    ...LocationInBackground,
  configure
};

export default  exportLocationInBackground  as LocationInBackgroundType;

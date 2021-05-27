import { NativeModules } from 'react-native';


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
  multiply(a: number, b: number): Promise<number>;
  startTracking(): void;
  stopTracking(): void;
  configure(config : ConfigType) : Promise<any>;
};

const { LocationInBackground } = NativeModules;


const configure = (config : ConfigType) => {
  config.httpHeaders =  config.httpHeaders ? JSON.stringify(config.httpHeaders) : undefined;
  config.extraPostData =  config.extraPostData ? JSON.stringify(config.extraPostData) : undefined;
  return LocationInBackground.configure(
      config
  )
};

const exportLocationInBackground = {
    ...LocationInBackground,
  configure
};

export default  exportLocationInBackground  as LocationInBackgroundType;

import { NativeModules } from 'react-native';

type LocationInBackgroundType = {
  multiply(a: number, b: number): Promise<number>;
};

const { LocationInBackground } = NativeModules;

export default LocationInBackground as LocationInBackgroundType;

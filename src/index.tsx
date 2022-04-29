import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-onfoot' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const Onfoot = NativeModules.Onfoot ? NativeModules.Onfoot
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const eventEmitter = new NativeEventEmitter(Onfoot);

export function observeSteps(from: string): Promise<any> {
  return Onfoot.observeSteps(from);
}

export function unobserveSteps(): Promise<any> {
  return Onfoot.unobserveSteps();
}

export function askPermissions(): Promise<any> {
  return Onfoot.askPermissions();
}

export function addListener(callback: any): any {
  return eventEmitter.addListener('step-av', event => {
    callback(event);
  });
}

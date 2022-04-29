package com.reactnativeonfoot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = OnfootModule.NAME)
public class OnfootModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Onfoot";

  public OnfootModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private Sensor sensor;
  private SensorManager sensorManager;
  private boolean registered;

  private SensorEventListener triggerEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      getReactApplicationContext()
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit("step-av", event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  @ReactMethod
  public void askPermissions(Promise promise) {
    int permissionState = ContextCompat.checkSelfPermission(getCurrentActivity(), Manifest.permission.ACTIVITY_RECOGNITION);

    if (permissionState != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getCurrentActivity(),
        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
        1);
    }

    promise.resolve(null);
  }

  @ReactMethod
  public void observeSteps(String startDate, Promise promise) {
    int permissionState = ContextCompat.checkSelfPermission(getCurrentActivity(), Manifest.permission.ACTIVITY_RECOGNITION);

    if (permissionState != PackageManager.PERMISSION_GRANTED) {
      promise.reject("Not permitted");
      return;
    }

    PackageManager manager = getReactApplicationContext().getPackageManager();
    boolean hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);

    if (!hasAccelerometer) {
      promise.reject("Step counter unavailable");
      return;
    }

    sensorManager = (SensorManager) getReactApplicationContext().getCurrentActivity().getSystemService(Context.SENSOR_SERVICE);
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    sensorManager.registerListener(triggerEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    this.registered = true;

    promise.resolve(null);
  }

  @ReactMethod
  public void unobserveSteps(Promise promise) {
    if (this.registered) {
      sensorManager.unregisterListener(triggerEventListener);
    }

    promise.resolve(null);
  }
}

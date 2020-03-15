
package com.xuexiang.xupdate;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNXUpdateModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNXUpdateModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNXUpdate";
  }
}
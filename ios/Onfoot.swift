import Foundation;
import React;
import CoreMotion;

extension DateFormatter {
    static var iSO8601DateWithMillisec: DateFormatter {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        return dateFormatter
    }
}

@objc(Onfoot)
class Onfoot: RCTEventEmitter {
  private let manager = CMMotionActivityManager();
  private let pedometer = CMPedometer();

  public override init() {
    super.init();
  }

  override func supportedEvents() -> [String]! {
    return ["step-av"]
  }

  @objc(askPermissions:rejecter:)
  public func askPermissions(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    resolve(nil);
  }

  @objc(unobserveSteps:rejecter:)
  private func unobserveSteps(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    pedometer.stopUpdates();
    resolve(nil);
  }

  @objc(observeSteps:resolver:rejecter:)
  public func observeSteps(startDate: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    if !CMPedometer.isStepCountingAvailable() {
        reject("0", "No pedometer available", nil);
        return;
    }
      
    let formater = DateFormatter.iSO8601DateWithMillisec
    let date = formater.date(from: startDate)
        
    guard let date = date else {
      debugPrint("Wrong date provided...", startDate);
      reject("0", "Date is not provided or is provided in incorrect format", nil);
      return;
    }
        
    pedometer.startUpdates(from: date) {
      [weak self] pedometerData, error in
        guard let pedometerData = pedometerData, error == nil else { return }

        DispatchQueue.main.async {
          self?.sendEvent(withName: "step-av", body: pedometerData.numberOfSteps.stringValue );
        }
    }
      
    resolve(nil);
  }
}

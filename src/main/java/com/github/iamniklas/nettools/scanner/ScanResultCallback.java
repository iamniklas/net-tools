package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.DeviceResult;

public interface ScanResultCallback {
    void onSuccessResult(DeviceResult result);
    void onErrorResult(Exception exception);
}

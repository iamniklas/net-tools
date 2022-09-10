package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.TestResult;

public interface ScanResultCallback {
    void onSuccessResult(TestResult result);
    void onErrorResult(Exception exception);
}

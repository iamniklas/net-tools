package com.github.iamniklas.nettools.models;

public class TestResult {
    public DeviceResult[] deviceResults;
    public long scanDuration;
    public long scanStart;
    public long scanFinish;

    public TestResult(DeviceResult[] _deviceResults, long _scanDuration, long _scanStart, long _scanFinish) {
        deviceResults = _deviceResults;
        scanDuration = _scanDuration;
        scanStart = _scanStart;
        scanFinish = _scanFinish;
    }
}

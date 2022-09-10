package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.models.DeviceResult;
import com.github.iamniklas.nettools.models.TestResult;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AcceleratedNetworkScanner extends Scanner {

    public AcceleratedNetworkScanner(String _networkId, int _port, String _path, RequestMethod _requestMethod, int _scanTimeout, ScanResultCallback _callback) {
        super(_networkId, _port, _path,  _requestMethod, _scanTimeout, _callback);
    }

    @Override
    public TestResult scanFor(Predicate<DeviceResult> condition) {
        ArrayList<RangeScanThread> scannerThreads = new ArrayList<>();
        int threadCount = 8;

        ArrayList<DeviceResult> deviceResults = new ArrayList<>();
        try {
            long measureStart = System.currentTimeMillis();
            for (int i = 0; i < threadCount; i++) {
                RangeScanThread scanThread = new RangeScanThread(
                        1 + (threadCount * (i * (threadCount / 2))),
                        Math.min((threadCount * (i + 1) * (threadCount / 2)), 255),
                        networkIdentifier,
                        scanTimeout,
                        deviceResults
                );
                scannerThreads.add(scanThread);
                scanThread.start();
            }
            for (int i = 0; i < threadCount; i++) {
                scannerThreads.get(i).join();
            }
            long measureFinish = System.currentTimeMillis();
            long scanDuration = measureFinish - measureStart;

            return new TestResult(deviceResults.toArray(new DeviceResult[0]), scanDuration, measureStart, measureFinish);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

    private class RangeScanThread extends Thread {

        private final int from;
        private final int to;
        private final String networkIdentifier;
        private final int scanTimeout;
        private final ArrayList<DeviceResult> deviceResults;

        public RangeScanThread(int _from, int _to, String _networkId, int _scanTimeout, ArrayList<DeviceResult> _Device_testResults) {
            from = _from;
            to = _to;
            networkIdentifier = _networkId;
            scanTimeout = _scanTimeout;
            deviceResults = _Device_testResults;
        }

        @Override
        public void run() {
            super.run();

            for (int i = from; i < to; i++) {
                DeviceResult deviceResult = testDevice(
                        String.format("%s://%s.%d:%d/%s", protocol, networkIdentifier, i, port, path),
                        String.format("%s.%s", networkIdentifier, i),
                        scanTimeout);
                if(deviceResult != null) {
                    deviceResults.add(deviceResult);
                }
            }
        }
    }
}

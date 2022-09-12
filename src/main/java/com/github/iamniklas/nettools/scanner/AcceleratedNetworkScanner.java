package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.models.DeviceResult;
import com.github.iamniklas.nettools.models.TestResult;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AcceleratedNetworkScanner extends Scanner {

    private Integer completedScans = 0;

    public AcceleratedNetworkScanner(String _networkId, int _port, String _path, RequestMethod _requestMethod, int _scanTimeout, ScanResultCallback _callback) {
        super(_networkId, _port, _path,  _requestMethod, _scanTimeout, _callback);
    }

    private synchronized void incrementScanCounter() {
        completedScans++;
        callback.onScanComplete(completedScans, 255);
    }

    @Override
    public TestResult scanFor(Predicate<DeviceResult> condition) {
        ArrayList<RangeScanThread> scannerThreads = new ArrayList<>();

        ArrayList<DeviceResult> deviceResults = new ArrayList<>();
        try {
            long measureStart = System.currentTimeMillis();
            for (int i = 0; i < 17; i++) {
                //System.out.printf("Thread Range: %d-%d%n", 1 + (i) * 15, (i+1) * 15);
                RangeScanThread scanThread = new RangeScanThread(
                        1 + (i) * 15,
                        (i+1) * 15,
                        networkIdentifier,
                        scanTimeout,
                        deviceResults
                );
                scannerThreads.add(scanThread);
                scanThread.start();
            }
            for (int i = 0; i < 17; i++) {
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

            for (int i = from; i <= to; i++) {
                DeviceResult deviceResult = testDevice(
                        String.format("%s://%s.%d:%d/%s", protocol, networkIdentifier, i, port, path),
                        String.format("%s.%s", networkIdentifier, i),
                        scanTimeout);
                incrementScanCounter();
                if(deviceResult != null) {
                    deviceResults.add(deviceResult);
                }
            }
        }
    }
}

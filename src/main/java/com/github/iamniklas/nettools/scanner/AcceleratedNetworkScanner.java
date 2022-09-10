package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.models.TestResult;

import java.awt.color.ICC_ColorSpace;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class AcceleratedNetworkScanner extends Scanner {

    public AcceleratedNetworkScanner(String _networkId, int _port, String _path, RequestMethod _requestMethod, int _scanTimeout, ScanResultCallback _callback) {
        super(_networkId, _port, _path,  _requestMethod, _scanTimeout, _callback);
    }

    @Override
    public TestResult[] scanFor(Predicate<TestResult> condition) {
        ArrayList<RangeScanThread> scannerThreads = new ArrayList<>();
        int threadCount = 8;

        ArrayList<TestResult> testResults = new ArrayList<>();
        try {
            for (int i = 0; i < threadCount; i++) {
                RangeScanThread scanThread = new RangeScanThread(
                        1 + (threadCount * (i * (threadCount / 2))),
                        Math.min((threadCount * (i + 1) * (threadCount / 2)), 255),
                        networkIdentifier,
                        scanTimeout,
                        testResults
                );
                scannerThreads.add(scanThread);
                scanThread.start();
            }
            for (int i = 0; i < threadCount; i++) {
                scannerThreads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return testResults.toArray(new TestResult[0]);
    }

    private class RangeScanThread extends Thread {

        private final int from;
        private final int to;
        private final String networkIdentifier;
        private final int scanTimeout;
        private final ArrayList<TestResult> testResults;

        public RangeScanThread(int _from, int _to, String _networkId, int _scanTimeout, ArrayList<TestResult> _testResults) {
            from = _from;
            to = _to;
            networkIdentifier = _networkId;
            scanTimeout = _scanTimeout;
            testResults = _testResults;
        }

        @Override
        public void run() {
            super.run();

            for (int i = from; i < to; i++) {
                TestResult deviceTestResult = testDevice(
                        String.format("%s://%s.%d:%d/%s", protocol, networkIdentifier, i, port, path),
                        String.format("%s.%s", networkIdentifier, i),
                        150);
                if(deviceTestResult != null) {
                    testResults.add(deviceTestResult);
                }
            }
        }
    }
}

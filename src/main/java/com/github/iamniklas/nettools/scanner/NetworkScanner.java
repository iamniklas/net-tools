package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.models.DeviceResult;
import com.github.iamniklas.nettools.models.TestResult;

import java.util.ArrayList;
import java.util.function.Predicate;

public class NetworkScanner extends Scanner {

    /**
     * IMPORTANT: NetScanner only works with HTTP, so this is mandatory - do not add 'http://' to the networkId, this will be added by the ctor
     * @param _networkId The first three numbers of the network's ip address (e.g. for a device with an ip of '10.0.8.115' the networkId would be '10.0.8'
     */
    public NetworkScanner(String _networkId, int _port, String _path, RequestMethod _requestMethod, int _scanTimeout, ScanResultCallback _callback) {
        super(_networkId, _port, _path, _requestMethod, _scanTimeout, _callback);
    }

    @Override
    public TestResult scanFor(Predicate<DeviceResult> condition) {
        testFor = condition;

        ArrayList<DeviceResult> results = new ArrayList<>();

        long measureStart = System.currentTimeMillis();
        for (int i = 1; i <= 255; i++) {
            DeviceResult result = testDevice(
                    String.format("%s://%s.%d:%d/%s", protocol, networkIdentifier, i, port, path),
                    String.format("%s.%s", networkIdentifier, i),
                    150);
            callback.onScanComplete(i, 255);
            if(result != null) {
                if(condition.test(result)) {
                    results.add(result);
                }
            }
        }
        long measureFinish = System.currentTimeMillis();
        long scanDuration = measureFinish - measureStart;

        DeviceResult[] deviceResults = results.toArray(new DeviceResult[0]);

        return new TestResult(deviceResults, scanDuration, measureStart, measureFinish);
    }


}

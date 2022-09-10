package com.github.iamniklas.nettools.scanner;

import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.models.DeviceResult;
import com.github.iamniklas.nettools.models.TestResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class Scanner {

    protected ArrayList<String> result = new ArrayList<>();
    protected Predicate<DeviceResult> testFor;

    protected final String protocol = "http";
    protected final String networkIdentifier;
    protected final Integer port;
    protected final String path;
    protected final RequestMethod requestMethod;
    protected final int scanTimeout;
    protected final ScanResultCallback callback;

    public static final int DEFAULT_TIMEOUT = 200;

    public Scanner(String _networkId, Integer _port, String _path, RequestMethod _requestMethod, int _scanTimeout, ScanResultCallback _callback) {
        networkIdentifier = _networkId;
        port = _port;
        path = _path;
        requestMethod = _requestMethod;
        scanTimeout = _scanTimeout;
        callback = _callback;
    }

    public Predicate<DeviceResult> getScanCondition() {
        return testFor;
    }

    /**
     *
     * @param _hostId The fourth number of the ip address. If null, the ip will be formatted as XXX.XXX.XXX.1
     * @return
     */
    public String getURL(Integer _hostId) {
        return String.format("%s://%s.%d:%d/%s", protocol, networkIdentifier, _hostId != null ? _hostId : 1, port, path);
    }

    public abstract TestResult scanFor(Predicate<DeviceResult> condition);

    protected DeviceResult testDevice(String _url, String _ip, int _timeout) {
        URL url = null;
        try {
            url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod.name());
            con.setConnectTimeout(_timeout);
            String body = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            DeviceResult result = new DeviceResult(_ip, con.getResponseCode(), body);
            callback.onSuccessResult(result);
            return result;
        } catch (Exception exception) {
            callback.onErrorResult(exception);
            return null;
        }
    }
}

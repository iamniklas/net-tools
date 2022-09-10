import com.github.iamniklas.nettools.models.TestResult;
import com.github.iamniklas.nettools.scanner.AcceleratedNetworkScanner;
import com.github.iamniklas.nettools.scanner.NetworkScanner;
import com.github.iamniklas.nettools.models.RequestMethod;
import com.github.iamniklas.nettools.scanner.ScanResultCallback;
import com.github.iamniklas.nettools.scanner.Scanner;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkScannerTests implements ScanResultCallback {
    NetworkScanner networkScanner;
    AcceleratedNetworkScanner acceleratedNetworkScanner;

    @BeforeEach
    public void initScanner() {
        networkScanner = new NetworkScanner("192.168.178", 5700, "device/name", RequestMethod.GET, Scanner.DEFAULT_TIMEOUT, this);
        acceleratedNetworkScanner = new AcceleratedNetworkScanner("192.168.178", 5700, "device/name", RequestMethod.GET, Scanner.DEFAULT_TIMEOUT, this);
    }

    @Test
    void testURLResolving() {
        Assertions.assertEquals(
                "http://192.168.178.1:5700/device/name",
                networkScanner.getURL(null)
        );
    }

    @Test
    void testNetworkScanner() {
        TestResult[] scanResult = networkScanner.scanFor(s -> s.resultCode == 200);
        List<String> ips = Arrays.stream(scanResult).map(r -> r.ip).collect(Collectors.toList());
        List<String> deviceNames = Arrays.stream(scanResult).map(r -> r.body).collect(Collectors.toList());
        System.out.println("Scan Complete // Results:");
        System.out.println("IPs:");
        System.out.println(new Gson().toJson(ips));
        System.out.println("Device Names:");
        System.out.println(new Gson().toJson(deviceNames));
        System.out.println("---");
    }

    @Test
    void testAcceleratedNetworkScanner() {
        TestResult[] scanResult = acceleratedNetworkScanner.scanFor(s -> s.resultCode == 200);
        List<String> ips = Arrays.stream(scanResult).map(r -> r.ip).collect(Collectors.toList());
        List<String> deviceNames = Arrays.stream(scanResult).map(r -> r.body).collect(Collectors.toList());
        System.out.println("Scan Complete // Results:");
        System.out.println("IPs:");
        System.out.println(new Gson().toJson(ips));
        System.out.println("Device Names:");
        System.out.println(new Gson().toJson(deviceNames));
        System.out.println("---");
    }

    @Override
    public void onSuccessResult(TestResult result) {
        System.out.println("Found device with IP " + result.ip);
    }

    @Override
    public void onErrorResult(Exception exception) {

    }
}

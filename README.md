# net-tools
 
Net-Tools provides the function to scan your local network for devices/addresses that matches a given condition.

Example (Scan for http servers):
```java
class Main {
   public static void main(String[] args) {
       TestResult testResult = scanForDevices(new ScanResultCallback() {
            @Override
            public void onSuccessResult(DeviceResult result) {
                // Implement your logic for onSuccessResult here
            }

            @Override
            public void onScanComplete(int progress, int maxValue) {
                System.out.println("PROGRESS: " + progress);
            }

            @Override
            public void onErrorResult(Exception exception) {
                // Implement your logic for onErrorResult here
            }
        });

       System.out.println(testResult.deviceResults);
   }

   public static TestResult scanForDevices(ScanResultCallback callback) {

        try(final DatagramSocket socket = new DatagramSocket()) {

            //Find out your local ip address
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String scanClientIp = socket.getLocalAddress().getHostAddress();

            int lastIndexOf = scanClientIp.lastIndexOf('.');
            String networkScanRange = scanClientIp.substring(0, lastIndexOf);

            AcceleratedNetworkScanner scanner = new AcceleratedNetworkScanner(
                    networkScanRange,
                    80,
                    "/index.html",
                    RequestMethod.GET,
                    Scanner.DEFAULT_TIMEOUT,
                    callback
            );

            return scanner.scanFor(t ->
                    t.resultCode == HttpURLConnection.HTTP_OK &&
                            t.body != null
            );
        }
        catch (Exception e) {
            return null;
        }
    }
}
```

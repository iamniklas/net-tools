package com.github.iamniklas.nettools.models;

public class TestResult {
    public final String ip;
    public final Integer resultCode;
    public final String body;

    public TestResult(String _ip, Integer _resultCode, String _body) {
        ip = _ip;
        resultCode = _resultCode;
        body = _body;
    }
}

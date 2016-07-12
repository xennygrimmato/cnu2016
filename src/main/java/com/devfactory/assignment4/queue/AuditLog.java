package com.devfactory.assignment4.queue;

import java.util.Map;

/**
 * Created by vaibhavtulsyan on 11/07/16.
 */
public class AuditLog {

    private String url;
    private String ipAddress;
    private long timestamp;
    private int responseCode;
    private Map<String, String> parameters;
    private String method;
    private String body;

    public AuditLog() {}

    public AuditLog(String url, String ipAddress, long timestamp, int responseCode, Map<String,String> parameters, String method, String body) {
        this.url = url;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.responseCode = responseCode;
        this.parameters = parameters;
        this.method = method;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }


    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}

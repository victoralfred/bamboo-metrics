package com.kezi.bamboo.metrics.model;

public class ServerStatus {
    private boolean passed;
    private String message;
    public boolean isPassed() {
        return passed;
    }
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "ServerStatus{" +
                "passed=" + passed +
                ", message='" + message + '\'' +
                '}';
    }
}

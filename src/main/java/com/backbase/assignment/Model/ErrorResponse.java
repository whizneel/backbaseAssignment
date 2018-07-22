package com.backbase.assignment.Model;

import java.util.List;

public class ErrorResponse {
    private String error;
    private List<String> errorMessages;
    private String apiStatus;

    public ErrorResponse(String error, List<String> errorMessages) {
        this.error = error;
        this.errorMessages = errorMessages;
    }

    public ErrorResponse(String error, List<String> errorMessages, String apiStatus) {
        this.error = error;
        this.errorMessages = errorMessages;
        this.apiStatus = apiStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", errorMessages=" + errorMessages +
                ", apiStatus='" + apiStatus + '\'' +
                '}';
    }
}
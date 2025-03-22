
package com.projectstack.api.payload.response;

public class MessageResponse {
    private String message;
    private boolean success;
    private String error;

    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
        this.error = null;
    }

    public MessageResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.error = success ? null : message;
    }

    public MessageResponse(String message, boolean success, String error) {
        this.message = message;
        this.success = success;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package com.sforce.profanitycheck.common;

import java.io.Serializable;

/**
 * Basic API response object used by modification requests.
 * TODO: extend with domain-specific data to also return modified objects?
 */
public class ApiResult implements Serializable {

    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean setFailure(String message) {
        this.message = message;
        this.success = false;
        return false;
    }

    public boolean setSuccess(String message) {
        this.message = message;
        this.success = true;
        return true;
    }
}

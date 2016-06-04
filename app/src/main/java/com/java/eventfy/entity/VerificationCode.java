package com.java.eventfy.entity;

/**
 * Created by swapnil on 5/29/16.
 */

public class VerificationCode {

    private String vCode;

    private String userId;

    public String getvCode() {
        return vCode;
    }

    public void setvCode(String vCode) {
        this.vCode = vCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}

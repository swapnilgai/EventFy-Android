package com.java.eventfy.Entity.UserAccount;

import com.java.eventfy.Entity.SignUp;

import java.io.Serializable;

/**
 * Created by swapnil on 2/22/17.
 */

public class VerifyAccount implements Serializable {
    private String viewMsg;
    private SignUp signUp;

    public SignUp getSignUp() {
        return signUp;
    }

    public void setSignUp(SignUp signUp) {
        this.signUp = signUp;
    }

    public String getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(String viewMsg) {
        this.viewMsg = viewMsg;
    }
}


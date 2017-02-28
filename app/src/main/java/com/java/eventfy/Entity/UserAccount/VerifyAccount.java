package com.java.eventfy.Entity.UserAccount;

import com.java.eventfy.Entity.SignUp;

import java.io.Serializable;

/**
 * Created by swapnil on 2/22/17.
 */

public class VerifyAccount implements Serializable {
    private String viewMsg;
    private SignUp signUp;
    private String activityName;

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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}


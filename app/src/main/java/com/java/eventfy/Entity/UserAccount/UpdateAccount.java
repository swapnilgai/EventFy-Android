package com.java.eventfy.Entity.UserAccount;

import com.java.eventfy.Entity.SignUp;

/**
 * Created by swapnil on 2/22/17.
 */

public class UpdateAccount {
    private double id;
    private SignUp signUp;
    private String viewMsg;

    public String getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(String viewMsg) {
        this.viewMsg = viewMsg;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public SignUp getSignUp() {
        return signUp;
    }

    public void setSignUp(SignUp signUp) {
        this.signUp = signUp;
    }

}

package com.java.eventfy.utils;

import com.java.eventfy.Entity.SignUp;

/**
 * Created by swapnil on 2/27/17.
 */
public class CleanEntityObjects {
    private static CleanEntityObjects ourInstance = new CleanEntityObjects();

    public static CleanEntityObjects getInstance() {
        return ourInstance;
    }

    private CleanEntityObjects() {
    }

    public SignUp clearSignUpObject(SignUp signUp){

        signUp.setPassword(null);
        signUp.setVerificationCode(null);
        signUp.setUserId(null);
        signUp.setComments(null);
        signUp.setEvents(null);
        signUp.setNotificationDetails(null);
        signUp.setLocation(null);

        return signUp;
    }

    public SignUp clearSignUpObjectForRequest(SignUp signUp){

        signUp.setPassword(null);
        signUp.setVerificationCode(null);
        signUp.setComments(null);
        signUp.setEvents(null);
        signUp.setNotificationDetails(null);
        signUp.setLocation(null);

        return signUp;
    }

}

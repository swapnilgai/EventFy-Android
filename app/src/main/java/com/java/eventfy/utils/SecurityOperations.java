package com.java.eventfy.utils;

import android.util.Log;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Created by swapnil on 10/27/16.
 */
public class SecurityOperations {

    StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();

    public String encryptNetworkPassword(String key)
    {

        if(!jasypt.isInitialized())
            jasypt.setPassword("testKey@123456@hklp6426356hksdgdgjv673yf@njn3#dnsj$njdsn&bh%hbHbh");
        // this is the authentication token user will send in order to use the web service
        String authenticationToken = jasypt.encrypt(key);

        return authenticationToken;
    }

    public boolean comparePassword(String inputPassword, String encryptedPassword) {

        Log.e("comp ;  "+inputPassword, "   with :   "+encryptedPassword);
        if(!jasypt.isInitialized())
            jasypt.setPassword("testKey@123456@hklp6426356hksdgdgjv673yf@njn3#dnsj$njdsn&bh%hbHbh");
        Log.e("decrypt : ", "decr : "+jasypt.decrypt(encryptedPassword));
        if(jasypt.decrypt(encryptedPassword).equals(inputPassword))
                return true;
        else
            return false;

    }


    public String decryptPassword(String password) {
        if(!jasypt.isInitialized())
            jasypt.setPassword("testKey@123456@hklp6426356hksdgdgjv673yf@njn3#dnsj$njdsn&bh%hbHbh");

       return jasypt.decrypt(password);
    }

}

package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.java.eventfy.Entity.UserAccount.PasswordReset;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 2/24/17.
 */

public class ResetPasswordAction extends AsyncTask<Void, Void, Void> {
    private String url;
    private PasswordReset passwordReset;
    private PasswordReset passwordResetTemp;

    public ResetPasswordAction(PasswordReset passwordReset, String url) {
        this.url = url;
        this.passwordReset = passwordReset;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        try {



            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();

            HttpEntity<PasswordReset> request = new HttpEntity<>(passwordReset, headers);

            ResponseEntity<PasswordReset> rateResponse = restTemplate.postForEntity(url, request, PasswordReset.class);
            passwordResetTemp = rateResponse.getBody();

        }catch (Exception e) {
            Log.e("Exception : ", ""+e.getStackTrace());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Gson g = new Gson();

        if(passwordResetTemp!=null) {
            EventBusService.getInstance().post(passwordResetTemp);
        }else{
            EventBusService.getInstance().post(passwordReset);
        }
    }
}

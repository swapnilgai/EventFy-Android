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

public class GetAccessCode  extends AsyncTask<Void, Void, Void> {
    private String url;
    private String result;
    private PasswordReset resetPassword;
    private PasswordReset resetPasswordTemp;

    public GetAccessCode(PasswordReset resetPassword, String url) {
        this.url = url;
        this.resetPassword = resetPassword;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        try {

            Gson g = new Gson();
            Log.e("obj :::   ", ""+g.toJson(resetPassword));

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();

            HttpEntity<PasswordReset> request = new HttpEntity<>(resetPassword, headers);

            ResponseEntity<PasswordReset> rateResponse = restTemplate.postForEntity(url, request, PasswordReset.class);
            resetPasswordTemp = rateResponse.getBody();


        }catch (Exception e) {
            Log.e("Exception : ", ""+e.getStackTrace());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(resetPasswordTemp!=null && resetPasswordTemp.getViewMsg()!=null)
                EventBusService.getInstance().post(resetPasswordTemp);
        else
            EventBusService.getInstance().post(resetPassword);
    }
}
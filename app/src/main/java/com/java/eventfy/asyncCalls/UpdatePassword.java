package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.UserAccount.PasswordReset;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 2/2/17.
 */

public class UpdatePassword extends AsyncTask<Void, Void, Void>  {
    private PasswordReset passwordReset;
    private String url;
    private String result;

    public UpdatePassword(PasswordReset passwordReset, String url) {
        this.passwordReset = passwordReset;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        try {

        Log.e(" url ", url);

        Gson gson = new Gson();

        Log.e(" obj :  ", gson.toJson(passwordReset));

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PasswordReset> request = new HttpEntity<>(passwordReset, headers);

        ResponseEntity<PasswordReset> rateResponse = restTemplate.postForEntity(url, request, PasswordReset.class);

        passwordReset = rateResponse.getBody();

        }catch (Exception e)
        {
            Log.e("exception : ", ""+e.getStackTrace());

            passwordReset.setViewMsg("Error");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        EventBusService.getInstance().post(passwordReset);
    }
}

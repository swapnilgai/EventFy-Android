package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.utils.CleanEntityObjects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/28/16.
 */
public class UpdateNotificationDetail   extends AsyncTask<Void, Void, Void> {
    private SignUp signUp;
    private String url;
    private String result;

    public UpdateNotificationDetail(SignUp signUp, String url) {
        this.signUp = signUp;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

       try {
           signUp = CleanEntityObjects.getInstance().clearSignUpObject(signUp);

           RestTemplate restTemplate = new RestTemplate(true);
           restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);

           HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

           ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
           result = rateResponse.getBody();

       }catch (Exception e){

       }
        return null;


    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       Log.e("data store success", ""+result);
    }
}
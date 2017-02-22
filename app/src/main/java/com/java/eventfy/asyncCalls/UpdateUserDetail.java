package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 12/23/16.
 */

public class UpdateUserDetail extends AsyncTask<Void, Void, Void> {
    private SignUp signUp;
    private String url;
    private String result;

    public UpdateUserDetail(SignUp signUp, String url) {
        this.signUp = signUp;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

       // try {

        Log.e(" url ", url);

        Gson g = new Gson();
        Log.e("obj : ", g.toJson(signUp));

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

        ResponseEntity<SignUp> rateResponse = restTemplate.postForEntity(url, request, SignUp.class);

        signUp = rateResponse.getBody();


//        }catch (Exception e)
//        {
//            Log.e("exception : ", ""+e.getStackTrace());
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(signUp==null) {
            signUp = new SignUp();
            signUp.setViewMessage("unsuccessfull");
        }

        EventBusService.getInstance().post(signUp);
    }
}

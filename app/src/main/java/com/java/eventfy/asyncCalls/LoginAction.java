package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.User;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/27/16.
 */
public class LoginAction extends AsyncTask<Void, Void, Void>  {

    private  SignUp signUp;
    private String url;
    private String result;
    private User login;

    public LoginAction(User login, String url)
    {
        this.login = login;
        this.url =url;
    }

    @Override
    protected Void doInBackground(Void... strings) {
                ObjectMapper mapper = new ObjectMapper();

        Log.e(" url ", url);

        Gson gson = new Gson();
        Log.e("Obj : ", ""+gson.toJson(login));

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> request = new HttpEntity<>(login, headers);


        try {
            ResponseEntity<SignUp> rateResponse = restTemplate.postForEntity(url, request, SignUp.class);
            signUp = rateResponse.getBody();
        }catch (Exception e){}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(signUp == null)
            signUp = new SignUp();
         // sending in LoginActivity
        EventBusService.getInstance().post(signUp);
    }
}

package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.User;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/27/16.
 */
public class LoginAction extends AsyncTask<Void, Void, Void>  {

    private  SignUp signUp;
    private String url;
    private Context context;
    private User login;
    private SimpleClientHttpRequestFactory factory;

    public LoginAction(User login, String url, Context context )
    {
        this.login = login;
        this.url =url;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... strings) {
                ObjectMapper mapper = new ObjectMapper();
        try{
        Log.e(" url ", url);

        Gson gson = new Gson();
        Log.e("Obj : ", ""+gson.toJson(login));

        RestTemplate restTemplate = new RestTemplate(true);
//        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(100000);

        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> request = new HttpEntity<>(login, headers);


        ResponseEntity<SignUp> rateResponse = restTemplate.postForEntity(url, request, SignUp.class);
        signUp = rateResponse.getBody();

        }catch (Exception e) {
            // connection timed out...let's try again

            if(signUp == null)
                signUp = new SignUp();
            signUp.setViewMessage(context.getString(R.string.home_connection_error));
        }
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

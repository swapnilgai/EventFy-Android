package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.eventfy.Entity.SignUp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Created by swapnil on 10/24/16.
 */
public class LoginAction  extends AsyncTask<Void, Void, Void> {
    private  SignUp signUp;
    private String url;
    private String result;

    public LoginAction(SignUp signUp, String url) {
        this.signUp = signUp;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        Log.e("url ", ""+url);
        Log.e("location is : ", ""+signUp.getLocation().getLongitude());

        RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
//Object to JSON in String
        try {
            String jsonInString = mapper.writeValueAsString(signUp);
            Log.e("onbject : ", ""+jsonInString);
            String jsonInString1 = mapper.writeValueAsString(signUp.getLocation());
            Log.e("onbject : ", ""+jsonInString1);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

            ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
            result = rateResponse.getBody();

            return null;

            }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("token is : ", ""+result);
    }
    }

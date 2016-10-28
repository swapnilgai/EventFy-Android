package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/26/16.
 */
public class VerifyVcode extends AsyncTask<Void, Void, Void> {
    private SignUp signUp;
    private String url;
    private String result;

    public VerifyVcode(SignUp signUp, String url) {
        this.signUp = signUp;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();

        HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

        ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
        result = rateResponse.getBody();

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(!(result!=null && result.equals(signUp.getToken())))
            signUp.setVerificationCode(null);

            EventBusService.getInstance().post(signUp);
    }
}

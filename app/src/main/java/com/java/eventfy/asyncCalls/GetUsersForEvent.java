package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by swapnil on 10/8/16.
 */
public class GetUsersForEvent  extends AsyncTask<Void, Void, Void> {

    private String url;
    private List<SignUp> userList;
    private String eventId;
    // private String flag;


    public GetUsersForEvent(String url, String eventId) {
        this.url = url;
        this.eventId = eventId;
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

            headers.add("Content-Type", "text/plain");

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<>(eventId, headers);

            ResponseEntity<SignUp[]> response = restTemplate.exchange(url, HttpMethod.POST, request, SignUp[].class);

            SignUp[] userListArray = response.getBody();

            userList = Arrays.asList(userListArray);
        }catch (Exception e) {

        }
            return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        EventBusService.getInstance().post(userList);
    }
}
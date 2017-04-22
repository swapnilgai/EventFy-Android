package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by swapnil on 12/2/16.
 */

public class GetNearByUsers   extends AsyncTask<Void, Void, Void> {


    private String url;
    private SignUp signUp;
    private List<SignUp> userList;
    private String flag;
    private Context context;
    public GetNearByUsers() {

    }

    public GetNearByUsers(String url, SignUp signUp, Context context){
        this.url = url;
        this.signUp = signUp;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            ResponseEntity<SignUp[]> response = restTemplate.exchange(url, HttpMethod.POST, request, SignUp[].class);

            SignUp[] signUpList = response.getBody();

            userList = Arrays.asList(signUpList);
        } catch (Exception e)
        {}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(userList ==null) {
            SignUp signUp = new SignUp();
            signUp.setViewMessage(context.getString(R.string.home_no_data));
            userList = new LinkedList<SignUp>();
            userList.add(signUp);
        }

        EventBusService.getInstance().post(userList);
    }
}

package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
        this.flag= flag;
    }



    @Override
    protected Void doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpEntity<SignUp> request = new HttpEntity<>(signUp);

        ResponseEntity<SignUp[]> response = restTemplate.exchange(url, HttpMethod.POST, request, SignUp[].class);

        SignUp[] signUpList = response.getBody();

        userList = Arrays.asList(signUpList);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(userList ==null) {
            SignUp signUp = new SignUp();
            signUp.setViewMessage(context.getResources().getString(R.string.home_no_data));
            userList.add(signUp);
        }

        Log.e("data received : ", " ***** "+userList.size());


        EventBusService.getInstance().post(userList);
    }
}

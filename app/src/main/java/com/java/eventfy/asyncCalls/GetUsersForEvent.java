package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by swapnil on 10/8/16.
 */
public class GetUsersForEvent  extends AsyncTask<Void, Void, Void> {

    private String url;
    private List<SignUp> userList;
    private String eventId;
    private Context context;
    private SignUp signUp;
    // private String flag;


    public GetUsersForEvent(String url, SignUp signUp, Context context) {
        this.url = url;
        this.signUp = signUp;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

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


        if(userList==null || userList.size()<=0) {
            userList = new ArrayList<SignUp>();
            SignUp signUp = new SignUp();
            signUp.setViewMessage(context.getString(R.string.home_no_data));
            userList.add(signUp);
        }

        EventBusService.getInstance().post(userList);
    }
}
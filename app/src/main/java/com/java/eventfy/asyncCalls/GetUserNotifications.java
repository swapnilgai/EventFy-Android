package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 12/16/16.
 */

public class GetUserNotifications extends  AsyncTask<Void, Void, Void> {

    private String url;
    private List<NotificationDetail> notificationDetailsList;
    private Context context;
    private SignUp signUp;
    // private String flag;


    public GetUserNotifications(String url, SignUp signUp, Context context) {
        this.url = url;
        this.signUp = signUp;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
//        try {
//
//            Gson g = new Gson();
//
//            RestTemplate restTemplate = new RestTemplate(true);
//            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//            HttpEntity<SignUp> request = new HttpEntity<>(signUp);
//
//            ResponseEntity<NotificationDetail[]> response = restTemplate.exchange(url, HttpMethod.POST, request, NotificationDetail[].class);
//
//            NotificationDetail[] userListArray = response.getBody();
//
//            notificationDetailsList = new LinkedList(Arrays.asList(userListArray));
//
//        }catch (Exception e) {
//
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        if(notificationDetailsList==null || notificationDetailsList.size()<=0) {
            notificationDetailsList = new ArrayList<NotificationDetail>();
            NotificationDetail notificationDetailObj = new NotificationDetail();
            notificationDetailObj.setViewMessage(context.getString(R.string.home_no_data));
            notificationDetailsList.add(notificationDetailObj);
        }
        Log.e("items from server : ", " 0000 "+notificationDetailsList.size());

        EventBusService.getInstance().post(notificationDetailsList);
    }
}

package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.utils.CleanEntityObjects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/28/16.
 */
public class UpdateNotificationDetail   extends AsyncTask<Void, Void, Void> {
    private SignUp signUp;
    private String url;
    private String result;
    private Context context;


    public UpdateNotificationDetail(SignUp signUp, String url, Context context) {
        this.signUp = signUp;
        this.url = url;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... strings) {

     //  try {

           signUp = CleanEntityObjects.getInstance().clearSignUpObject(signUp);

           RestTemplate restTemplate = new RestTemplate(true);
           restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);

           HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

           ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
           result = rateResponse.getBody();

//       }catch (Exception e){
//            result = null;
//       }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.e("  ", " signup :   "+new Gson().toJson(signUp));

        Log.e("  ", " result :   "+result);

        if(signUp.getNotificationId()!= null){
            NotificationId notificationId = new NotificationId();
            signUp.setNotificationId(notificationId);
        }

        if(result == null)
          signUp.getNotificationId().setViewMessage(context.getString(R.string.notification_id_server_register_success));
        else
            signUp.getNotificationId().setViewMessage(result);

        EventBusService.getInstance().post(signUp.getNotificationId());

    }
}
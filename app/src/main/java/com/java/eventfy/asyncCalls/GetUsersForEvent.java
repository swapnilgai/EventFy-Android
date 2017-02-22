package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.SignUp;

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
//        try {
//
//            Gson g = new Gson();
//            Log.e("get users : ", " #### "+g.toJson(signUp));
//
//            RestTemplate restTemplate = new RestTemplate(true);
//            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//            HttpEntity<SignUp> request = new HttpEntity<>(signUp);
//
//            ResponseEntity<SignUp[]> response = restTemplate.exchange(url, HttpMethod.POST, request, SignUp[].class);
//
//            SignUp[] userListArray = response.getBody();
//
//            userList = Arrays.asList(userListArray);
//
//        }catch (Exception e) {
//
//        }
            return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

//
//        if(userList==null || userList.size()<=0) {
//            userList = new ArrayList<SignUp>();
//            SignUp signUp = new SignUp();
//            signUp.setViewMessage(context.getString(R.string.home_no_data));
//            userList.add(signUp);
//        }
//
//        EventBusService.getInstance().post(userList);
    }
}
package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/26/16.
 */
public class VerifyMyAccount extends AsyncTask<Void, Void, Void> {
    private SignUp signUp;
    private VerifyAccount verifyAccount;
    private String url;
    private String result;
    private Context context;

    public VerifyMyAccount(VerifyAccount verifyAccount, String url, Context context) {
        this.verifyAccount = verifyAccount;
        this.url = url;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        try {
            signUp = verifyAccount.getSignUp();

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();

            HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

            ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
            result = rateResponse.getBody();

        }catch (Exception e)
        {
            result = null;
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       if(result == null)
           verifyAccount.setViewMsg(context.getString(R.string.verify_account_fail));


        Log.e("message : ", ""+result);
        // send to signup activity

        EventBusService.getInstance().post(verifyAccount);
    }
}

package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.User;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/27/16.
 */
public class LoginAction extends AsyncTask<Void, Void, Void>  {

    private  SignUp signUp;
    private String url;
    private String result;
    private User login;

    public LoginAction(User login, String url)
    {
        this.login = login;
        this.url =url;
    }

    @Override
    protected Void doInBackground(Void... strings) {

        try {
            //ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

//        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
//        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

            HttpEntity<User> request = new HttpEntity<>(login);

            ResponseEntity<SignUp> rateResponse = restTemplate.postForEntity(url, request, SignUp.class);

            signUp = rateResponse.getBody();

            Log.e("object is ", "" + signUp.getToken());
        }catch (Exception e){
                Log.e("in exception", "****");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(signUp == null)
            signUp = new SignUp();
         // sending in LoginActivity
        EventBusService.getInstance().post(signUp);
    }
}

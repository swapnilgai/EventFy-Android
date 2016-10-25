package com.java.eventfy;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.CallbackManager.Factory;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.asyncCalls.LoginAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private LoginButton fbLoginBt;
    private CallbackManager callbackManager;
    private SignUp signUp;
    private EditText emailText;
    private  EditText passwordText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        fbLoginBt = (LoginButton) findViewById(R.id.facebook_login_button);
        fbLoginBt.setReadPermissions("email");

        fbLoginBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onFblogin();

            }
        });
    }

    // Private method to handle Facebook login and callback
    private void onFblogin() {
        callbackManager = Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                                                GraphRequest request = GraphRequest.newMeRequest(
                                                        loginResult.getAccessToken(),
                                                        new GraphRequest.GraphJSONObjectCallback() {
                                                            @Override
                                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                                // Insert your code here
                                                                // Application code
                                                                try {

                                                                    String jsonresult = String.valueOf(object);

                                                                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                                    String Address = object.getJSONObject("location").getString("name");

                                                                    LatLng latLng = getLocationFromAddress(Address);

                                                                    signUp = new SignUp();
                                                                    signUp.setUserName(object.getString("name"));
                                                                    signUp.setDob(object.getString("birthday"));
                                                                    signUp.setImageUrl(profilePicUrl);
                                                                    signUp.setIsFacebook(true);
                                                                    signUp.setUserId(object.getString("email"));


                                                                    if(latLng!=null) {
                                                                        Location location = new Location();
                                                                        location.setLatitude(latLng.latitude);
                                                                        location.setLongitude(latLng.longitude);
                                                                        location.setDistance(10);
                                                                        signUp.setLocation(location);

                                                                    }

                                                                    serverCall(signUp);

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });

                                                Bundle parameters = new Bundle();
                                                parameters.putString("fields", "id,name,email,birthday,location,picture,gender");
                                                request.setParameters(parameters);
                                                request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        //facebookErrorMsg();
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());


        } catch (IOException e) {
            e.printStackTrace();
        }

        return p1;
    }


    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void serverCall(SignUp signUp) {

        String url = getResources().getString(R.string.ip_localhost)+getResources().getString(R.string.login_action);
        LoginAction loginAction = new LoginAction(signUp,url);
        loginAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}

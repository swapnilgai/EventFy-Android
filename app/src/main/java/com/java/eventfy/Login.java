package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
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
import com.google.gson.Gson;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.User;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.LoginAction;
import com.java.eventfy.asyncCalls.SignUpAction;
import com.java.eventfy.utils.SecurityOperations;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity  {

    private LoginButton fbLoginBt;
    private CallbackManager callbackManager;
    private SignUp signUp;
    private EditText emailText;
    private EditText passwordText;
    private TextView signupLink;
    private TextView forgotPasswordLink;
    private Button loginButton;
    private User user;
    private String passwordTemp;
    private SecurityOperations securityOperations;
    private ProgressDialog progressDialog;
    private RobotoTextView invalidUsernamePasswordMsg;

    public ProgressDialog getProgressDialog() {
            return progressDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        setProgressDialog();

        if(getUserObject()!=null){
            redirectToHomeActivity();
        }

        fbLoginBt = (LoginButton) findViewById(R.id.facebook_login_button);
        fbLoginBt.setReadPermissions("email");

        fbLoginBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onFblogin();

            }
        });

        signupLink = (TextView) findViewById(R.id.link_signup);
        emailText = (EditText) findViewById(R.id.input_email);
        loginButton = (Button) findViewById(R.id.btn_login);
        passwordText = (EditText) findViewById(R.id.input_password);
        forgotPasswordLink  = (TextView) findViewById(R.id.link_reset_password);
        invalidUsernamePasswordMsg  = (RobotoTextView) findViewById(R.id.invalid_username_password);

        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginAction();
                    handled = true;
                }
                return handled;
            }
        });


        signupLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        forgotPasswordLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginAction();
            }
        });
    }

    private void loginAction() {
        //TODO write action for login button

        if(validate())
        {
            startProgressDialog();
            user = new User();
            user.setUsername(emailText.getText().toString());
            user.setPassword(passwordText.getText().toString());
            serverCallLogin(user);
        }

    }

    public void redirectToHomeActivity() {

        dismissProgressDialog();
        finish();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void setProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog() {
        progressDialog.show();
    }


    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
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
                                                                    Log.e("signup : ", jsonresult);

                                                                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                                                    String Address = object.getJSONObject("location").getString("name");

                                                                    LatLng latLng = getLocationFromAddress(Address);

                                                                    signUp = new SignUp();
                                                                    signUp.setUserName(object.getString("name"));
                                                                    signUp.setDob(object.getString("birthday"));
                                                                    signUp.setImageUrl(profilePicUrl);
                                                                    signUp.setIsFacebook("true");
                                                                    signUp.setUserId(object.getString("email"));
                                                                    signUp.setVisibilityMiles(10);
                                                                    signUp.setFacebookId(object.getString("id"));
                                                                    signUp.setVisibilityMode(getString(R.string.visibility_mode_visible));

                                                                    if(latLng!=null) {
                                                                        Location location = new Location();
                                                                        location.setLatitude(latLng.latitude);
                                                                        location.setLongitude(latLng.longitude);
                                                                        location.setDistance(10);
                                                                        signUp.setLocation(location);

                                                                    }

                                                                    serverCallFbLogin(signUp);

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

    private void serverCallFbLogin(SignUp signUp) {

        Log.e(" ----- ", " -------------- ");

        Log.e("signup : ", new Gson().toJson(signUp));
        LoginManager.getInstance().logOut();
        invalidUsernamePasswordMsg.setVisibility(View.GONE);
        String url = getString(R.string.ip_local)+getString(R.string.login_action_facebook);
        SignUpAction loginAction = new SignUpAction(signUp,url);
        loginAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void serverCallLogin(User user) {

        invalidUsernamePasswordMsg.setVisibility(View.GONE);
        passwordTemp = user.getPassword();
        securityOperations = new SecurityOperations();
        user.setPassword(securityOperations.encryptNetworkPassword(user.getPassword()));
        String url = getString(R.string.ip_local)+getString(R.string.login);
        LoginAction loginAction = new LoginAction(user,url, getApplicationContext());
        loginAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getUserObject(SignUp signUp)
    {
        dismissProgressDialog();
        if(signUp!=null && signUp.getViewMessage().equals(getString(R.string.login_success)))
        {
            EventBusService.getInstance().unregister(this);
            Intent intent = new Intent(this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("user", signUp);
            startActivity(intent);
        }
        else if(signUp!=null && signUp.getViewMessage().equals(getString(R.string.login_fail))){
            Toast.makeText(Login.this, "Invalid Username/ Password", Toast.LENGTH_LONG).show();
            invalidUsernamePasswordMsg.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(Login.this, "Server error, please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBusService.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
    }

    private String getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");

        if(json!=null && json.length()<100)
            json = null;

        Log.e("user is : ", ""+json);

        return json;
    }

}


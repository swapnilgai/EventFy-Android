package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.SignUpAction;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity implements OnDateSetListener {

    private static final String TAG = "SignupActivity";

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private TextView dobtext;
    private Button signupButton;
    private TextView loginLink;
    private SignUp signUp;
    public static final String DATEPICKER_TAG = "datepicker";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(SignUpActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate());

        nameText = (EditText) findViewById(R.id.input_name);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText =  (EditText) findViewById(R.id.input_password);
        dobtext = (EditText) findViewById(R.id.input_DOB);
        signupButton =(Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);


        if(signUp!=null)
            signupButton.setText("Update");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp signUpTemp = createSignUpObjectFromFormDetail();

                if(signUp!=null
                        && signUpTemp.getUserName().equals(signUp.getUserName())
                        && signUpTemp.getDob().equals(signUp.getDob())
                        && signUpTemp.getUserId().equals(signUp.getUserId())
                        && signUpTemp.getPassword().equals(signUp.getPassword()))
                {
                    EventBusService.getInstance().post(signUp);
                }
                else {
                    signupAction();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
                overridePendingTransition(0, 0);
            }
        });

        dobtext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1910, calendar.get(Calendar.YEAR) - 10);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
    }

    private void signupAction() {

        if(validate())
        {
            signUp = createSignUpObjectFromFormDetail();
            serverCall(signUp);
            setProgressDialog();
        }
    }


    public SignUp createSignUpObjectFromFormDetail(){
        SignUp signUp = new SignUp();
        signUp.setUserId(emailText.getText().toString());
        signUp.setPassword(passwordText.getText().toString());
        signUp.setIsVerified("false");
        signUp.setIsFacebook("false");
        signUp.setImageUrl("default");
        signUp.setDob(dobtext.getText().toString());
        signUp.setUserName(nameText.getText().toString());
        return signUp;
    }

    private boolean isVibrate() {
        return false;
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    private boolean isCloseOnSingleTapMinute() {
        return false;
    }

    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Log.e("date time ; ", ""+dobtext);

        dobtext.setText(year + "-" + month + "-" + day);

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


    @Subscribe
    public void getUserobject(SignUp signUp)
    {
        Log.e("in signupact string ", "&&&&&&&");
        dismissProgressDialog();
        if(signUp!=null && signUp.getToken()!=null) {
            this.signUp = signUp;
            EventBusService.getInstance().unregister(this);
            Intent intent = new Intent(this, VerifySignUp.class);
            intent.putExtra("user", signUp);
            startActivity(intent);
        }
        else {
            Toast.makeText(SignUpActivity.this, "Email already present", Toast.LENGTH_LONG).show();
        }
        this.signUp = signUp;
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String dob = dobtext.getText().toString();

        if (name.isEmpty() || name.length() < 3 ) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (dob.isEmpty()){
            dobtext.setError("Enter a valid Date of Birth");
            valid = false;}
        else
        { dobtext.setError(null);}


        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(null);

        } else {
            emailText.setError("enter a valid email address");
            valid = false;
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

        if(validate()) {
            String url = getString(R.string.ip_local) + getString(R.string.signup_adduser);
            SignUpAction loginAction = new SignUpAction(signUp, url);
            loginAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public void setProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }


}

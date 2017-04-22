package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.SignUpAction;
import com.java.eventfy.utils.DateTimeStringOperations;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity implements OnDateSetListener {


    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private TextView dobtext;
    private Button signupButton;
    private TextView loginLink;
    private SignUp signUp = new SignUp();
    public static final String DATEPICKER_TAG = "datepicker";
    private ProgressDialog progressDialog;
    private String dobString;
    private RobotoTextView dobErrorMsg;

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

        dobErrorMsg = (RobotoTextView) findViewById(R.id.link_dob_error);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp signUpTemp = createSignUpObjectFromFormDetail();
                    signupAction();

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
                datePickerDialog.setYearRange(1910, calendar.get(Calendar.YEAR) - 13);
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

        signUp.setUserId(emailText.getText().toString());
        signUp.setPassword(passwordText.getText().toString());
        signUp.setIsVerified("false");
        signUp.setIsFacebook("false");
        signUp.setImageUrl("default");
        signUp.setUserName(nameText.getText().toString());
        signUp.setVisibilityMiles(10);
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
        dobString = year + "-" + month + "-" + day;

        signUp.setDob(dobString);

        dobErrorMsg.setText(getString(R.string.minimum_age));

        dobErrorMsg.setVisibility(View.GONE);

        dobtext.setText(DateTimeStringOperations.getInstance().getDateString(dobString));
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
    public void getUserObject(SignUp signUp)
    {
        dismissProgressDialog();
        if(signUp!=null && signUp.getViewMessage().equals(getString(R.string.signup_success))) {
            this.signUp = signUp;
            EventBusService.getInstance().unregister(this);
            Intent intent = new Intent(this, VerifySignUp.class);
            intent.putExtra(getString(R.string.userObject), signUp);
            startActivity(intent);
        }
        else if(signUp!=null && signUp.getViewMessage().equals(getString(R.string.signup_account_already_present))) {
            toastMsg("Email already present");
        }
        else if(signUp!=null && signUp.getViewMessage().equals(getString(R.string.signup_server_error))){
            toastMsg("Error at server, please try again");
    }
        signUp.setViewMessage(null);
        this.signUp = signUp;
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3 ) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }
        if (signUp.getDob()!= null && signUp.getDob().length()>0 && !DateTimeStringOperations.getInstance().checkUSerIs18Plus(signUp.getDob())){

            if(signUp.getDob()==null){
                dobErrorMsg.setText("Date of birth cant be empty");
            }

            dobErrorMsg.setVisibility(View.VISIBLE);
            dobtext.setError("Minimum age to SignUp is 13");
            valid = false;
        }
        else {
            dobtext.setError(null);
            dobErrorMsg.setVisibility(View.GONE);
        }


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

    public void toastMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

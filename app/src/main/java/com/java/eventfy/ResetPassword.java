package com.java.eventfy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.java.eventfy.Entity.UserAccount.PasswordReset;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.GetAccessCode;
import com.java.eventfy.asyncCalls.ResetPasswordAction;
import com.java.eventfy.utils.SecurityOperations;

import org.greenrobot.eventbus.Subscribe;

public class ResetPassword extends AppCompatActivity {

    private EditText userId;
    private EditText vCode;
    private EditText newPassword;
    private EditText confirmPhone;
    private Button submitUserId;
    private Button submitUserPassword;
    private LinearLayout accessCodeLinearLayout;
    private LinearLayout passwordResetLinearLayout;
    private PasswordReset passwordReset;
    private TextView signUpLink;
    private SecurityOperations securityOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        userId = (EditText) findViewById(R.id.input_email_or_number);
        vCode = (EditText) findViewById(R.id.vcode_editText);
        newPassword = (EditText) findViewById(R.id.input_new_password);
        confirmPhone = (EditText) findViewById(R.id.input_confirm_password);
        submitUserId = (Button) findViewById(R.id.btn_submit);
        submitUserPassword = (Button) findViewById(R.id.btn_reset_password);
        accessCodeLinearLayout = (LinearLayout) findViewById(R.id.access_code);
        passwordResetLinearLayout = (LinearLayout) findViewById(R.id.verify_password);
        signUpLink = (TextView) findViewById(R.id.link_signup);

        passwordReset = new PasswordReset();

        passwordResetLinearLayout.setVisibility(View.GONE);

        securityOperations = new SecurityOperations();

        signUpLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });


        submitUserId.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                passwordReset.setUserId(userId.getText().toString());
                serverCallToGetAccessCode();

            }
        });


        submitUserPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(newPassword.getText().toString().equals(confirmPhone.getText().toString())) {
                    passwordReset.setAccessCode(vCode.getText().toString());
                    passwordReset.setNewPassword(securityOperations.encryptNetworkPassword(newPassword.getText().toString()));
                    serverCallToResetPassword();
                }
                else{
                    confirmPhone.setError("Password don't match");
                }
            }
        });
    }


    public void serverCallToGetAccessCode(){
        String url = getString(R.string.ip_local) + getString(R.string.user_get_access_code);
        GetAccessCode getAccessCode = new GetAccessCode(passwordReset, url);
        getAccessCode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void serverCallToResetPassword(){
        String url = getString(R.string.ip_local) + getString(R.string.user_reset_password);;
        ResetPasswordAction resetPasswordAction = new ResetPasswordAction(passwordReset, url);
        resetPasswordAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getPasswordResetObj(PasswordReset passwordReset){

        if(passwordReset.getViewMsg()!= null) {
            if (passwordReset.getViewMsg().equals(getString(R.string.reset_password_success))) {
                // TODO apply login logic to show home screen
                EventBusService.getInstance().unregister(this);
                Intent intent = new Intent(this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user", passwordReset.getSignUp());
                startActivity(intent);

            } else if (passwordReset.getViewMsg().equals(getString(R.string.access_code_generation_success))) {
                accessCodeLinearLayout.setVisibility(View.GONE);
                passwordResetLinearLayout.setVisibility(View.VISIBLE);
            } else if (passwordReset.getViewMsg().equals(getString(R.string.access_code_generation_fail))) {
                Toast.makeText(ResetPassword.this, "Account not present, please signup to start", Toast.LENGTH_LONG).show();
                signUpLink.setVisibility(View.VISIBLE);
            } else if ((passwordReset.getViewMsg().equals(R.string.access_code_generation_error)
                    || passwordReset.getViewMsg().equals(R.string.reset_password_server_error))) {

                Toast.makeText(ResetPassword.this, "Server error, please try again", Toast.LENGTH_LONG).show();

            } else if (passwordReset.getViewMsg().equals(R.string.reset_password_fail)) {
                Toast.makeText(ResetPassword.this, "Wrong code, please try again", Toast.LENGTH_LONG).show();
            }
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
}

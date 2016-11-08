package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.VerificationCode;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.ResendVcode;
import com.java.eventfy.asyncCalls.VerifyVcode;

import org.greenrobot.eventbus.Subscribe;

public class VerifySignUp extends AppCompatActivity {

    private TextView mCodeEditText;
    private Button mCodeSendButton;
    private ProgressDialog progressDialog;
    private SignUp signUp;
    private Button editBtn;
    private TextView userName;
    private TextView userId;
    private TextView dob;
    private VerificationCode verificationCode;
    private TextView resendVcodelink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sign_up);

        Intent in = getIntent();
        signUp = (SignUp) in.getSerializableExtra("user");

        if(!EventBusService.getInstance().isRegistered(this))
             EventBusService.getInstance().register(this);

        createProgressDialog();

        mCodeEditText = (TextView) findViewById(R.id.vcode_editText);
        mCodeSendButton = (Button) findViewById(R.id.vcode_button);
        editBtn = (Button) findViewById(R.id.edit_btn);
        userName = (TextView) findViewById(R.id.userName);
        userId = (TextView) findViewById(R.id.userId);
        dob = (TextView) findViewById(R.id.dob);
        resendVcodelink = (TextView) findViewById(R.id.link_resend_vcode);

        userName.setText(signUp.getUserName());
        userId.setText(signUp.getUserId());
        dob.setText(signUp.getDob());

        mCodeSendButton.setEnabled(true);

        mCodeSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());

                    if (mCodeEditText!=null && mCodeEditText.length()==4) {
                        mCodeSendButton.setEnabled(false);

                        verificationCode = new VerificationCode();
                        verificationCode.setvCode(mCodeEditText.getText().toString());
                        signUp.setVerificationCode(verificationCode);
                        serverCall(signUp);
                    }
                else{
                        mCodeSendButton.setError("Invalid Verification Code");
                    }
            }

        });


        resendVcodelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverCallToResendVcode(signUp);
            }
        });
        editBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
            finish();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
           EventBusService.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
    }

    //From signup activity async call
    @Subscribe
    public void getUserObject(SignUp signUp)
    {
        Log.e("in verifysignup signup ", "&&&&&&&");
        dismissProgressDialog();
        if(signUp.getVerificationCode()!=null)
        {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("user", signUp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Inavlid Verification Code", Toast.LENGTH_LONG);
        }
    }
    //From resend asyncall
@Subscribe
public void resendVcodeStatus(String result)
{
    Log.e("in verifysignup string ", "&&&&&&&");
        dismissProgressDialog();
}

    private void serverCall(SignUp signUp) {
        setProgressDialog();
        String url = getResources().getString(R.string.ip_local)+getResources().getString(R.string.verify_vcode);
        VerifyVcode verifyVcode = new VerifyVcode(signUp,url);
        verifyVcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void serverCallToResendVcode(SignUp signUp) {
        setProgressDialog();
        String url = getResources().getString(R.string.ip_local)+getResources().getString(R.string.verification_code_get);
        ResendVcode resendVcode = new ResendVcode(signUp,url);
        resendVcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void createProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);
    }

    public void setProgressDialog()
    {
        Log.e("in create view ", "**********");
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        Log.e("in dismiss view ", "++++++++");
        progressDialog.dismiss();
    }

}

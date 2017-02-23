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
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.Entity.VerificationCode;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.ResendVcode;
import com.java.eventfy.asyncCalls.VerifyMyAccount;
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
    private VerifyAccount verifyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sign_up);

        Intent in = getIntent();
        signUp = (SignUp) in.getSerializableExtra("user");

        verifyAccount = (VerifyAccount) in.getSerializableExtra(getString(R.string.verify_account));

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
        mCodeSendButton.setEnabled(true);

        if(verifyAccount!=null){
            serverCallToResendVcode(verifyAccount.getSignUp());
            setUserData(verifyAccount.getSignUp());
        }else {
            setUserData(signUp);
        }


        mCodeSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
            if(signUp!=null){
                verifyAccountViaSignUp();
            }
            else if(verifyAccount != null){
                verifyAccountViaMyAccount();
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

    public void setUserData(SignUp signUp){
        userName.setText(signUp.getUserName());
        userId.setText(signUp.getUserId());
        dob.setText(signUp.getDob());
    }

    public void verifyAccountViaSignUp(){
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

    public void verifyAccountViaMyAccount(){
        if (mCodeEditText!=null && mCodeEditText.length()==4) {
            mCodeSendButton.setEnabled(false);
            verificationCode = new VerificationCode();
            verificationCode.setvCode(mCodeEditText.getText().toString());
            verifyAccount.getSignUp().setVerificationCode(verificationCode);
            serverCallToVerifyAccount(verifyAccount);
        }
        else{
            mCodeSendButton.setError("Invalid Verification Code");
        }
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

    @Subscribe
    public void resendVcodeStatus(VerifyAccount verifyAccount) {
        dismissProgressDialog();

        if(verifyAccount.getViewMsg()!= null && verifyAccount.getViewMsg().equals(getString(R.string.verify_account_fail))){
            toastMsg("Error while verifying account");
        }
        else{
            toastMsg("Congratulations, you'r account successfully updated");
            finish();
        }
    }


    public void toastMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void serverCall(SignUp signUp) {
        setProgressDialog();
        String url = getString(R.string.ip_local)+getString(R.string.verify_vcode);
        VerifyVcode verifyVcode = new VerifyVcode(signUp,url);
        verifyVcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void serverCallToResendVcode(SignUp signUp) {
        if(verifyAccount==null)
            setProgressDialog();
        String url = getString(R.string.ip_local)+getString(R.string.verification_code_get);
        ResendVcode resendVcode = new ResendVcode(signUp,url);
        resendVcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void serverCallToVerifyAccount(VerifyAccount verifyAccount) {
        setProgressDialog();
        String url = getString(R.string.ip_local)+getString(R.string.verify_vcode);
        VerifyMyAccount verifyMyAccount = new VerifyMyAccount(verifyAccount,url, getApplicationContext());
        verifyMyAccount.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }

}

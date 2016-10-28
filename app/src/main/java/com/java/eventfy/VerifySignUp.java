package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.VerificationCode;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.VerifyVcode;

import org.greenrobot.eventbus.Subscribe;

public class VerifySignUp extends AppCompatActivity {

    private TextView mCodeEditText;
    private Button mCodeSendButton;
    private ProgressDialog progressDialog;
    private SignUp signUp;
    private VerificationCode verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sign_up);

        Intent in = getIntent();
        signUp = (SignUp) in.getSerializableExtra("user");

        EventBusService.getInstance().register(this);

        mCodeEditText = (TextView) findViewById(R.id.vcode_editText);
        mCodeSendButton = (Button) findViewById(R.id.vcode_button);
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

    @Subscribe
    public void getUserObject(SignUp signUp)
    {
        if(signUp.getVerificationCode()!=null)
        {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("user", signUp);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Inavlid Verification Code", Toast.LENGTH_LONG);
        }
    }

    private void serverCall(SignUp signUp) {

        String url = getResources().getString(R.string.ip_local)+getResources().getString(R.string.verify_vcode);
        VerifyVcode verifyVcode = new VerifyVcode(signUp,url);
        verifyVcode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}

package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.UpdateUserDetail;
import com.java.eventfy.utils.SecurityOperations;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import at.markushi.ui.CircleButton;

public class ProfilePage extends AppCompatActivity {

    private EditText usetStatus;
    private EditText usetName;
    private EditText usetEmail;
    private EditText usetDob;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private SeekBar userVisibilityMiles;
    private RadioGroup userVisibilityMode;
    private SignUp signUp;
    private RadioButton visibilityModeVisible, visibilityModeInVisible, visibilityModeDoNotDisturb;
    private Button saveButton;
    private UpdateUserDetail updateUserDetail;
    private ProgressDialog progressDialog;
    private  LinearLayout linearLayoutStatus;
    private CircleButton statusUpdateBtn;

    private ImageView userProfilePic;

    private SecurityOperations securityOperations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getUserObject();
        setProgressDialog();
        EventBusService.getInstance().register(this);

        userVisibilityMode = (RadioGroup) findViewById(R.id.user_visibility_mode);
        usetStatus = (EditText) findViewById(R.id.user_status);
        usetName = (EditText) findViewById(R.id.user_name);
        usetEmail = (EditText) findViewById(R.id.user_id);
        usetDob = (EditText) findViewById(R.id.user_dob);
        userPassword = (EditText) findViewById(R.id.user_password);
        userConfirmPassword = (EditText) findViewById(R.id.user_confirm_password);
        userVisibilityMiles =  (SeekBar) findViewById(R.id.user_visibility_miles);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);
        linearLayoutStatus = (LinearLayout) findViewById(R.id.user_status_linear_layout);
        visibilityModeVisible = (RadioButton) findViewById(R.id.user_visible);
        visibilityModeInVisible = (RadioButton) findViewById(R.id.user_invisible);
        visibilityModeDoNotDisturb = (RadioButton) findViewById(R.id.user_donotdisturb);
        linearLayoutStatus.setEnabled(false);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);
        statusUpdateBtn = (CircleButton) findViewById(R.id.status_edit_btn);

        userProfilePic = (ImageView) findViewById(R.id.user_profile_pic);

        securityOperations = new SecurityOperations();

        statusUpdateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordALertBox();

                Log.e("in edit text : ","(((((((( " );

            }
        });


        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getUpdatedUserData();
                serverCallToUpdateUserDetail();
            }
        });

if(signUp!=null && signUp.getUserId()!=null)
        setUserData();
    }



    public void getVisibilityMode() {


        userVisibilityMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.user_visible:
                        signUp.setVisibilityMode(getString(R.string.visibility_mode_visible));
                        // do operations specific to this selection
                        break;
                    case R.id.user_donotdisturb:
                        signUp.setVisibilityMode(getString(R.string.visibility_mode_donotdisturb));
                        // do operations specific to this selection
                        break;
                    case R.id.user_invisible:
                        signUp.setVisibilityMode(getString(R.string.visibility_mode_invisible));
                        // do operations specific to this selection
                        break;
                }
            }
        });

    }


    public void setUserData() {


        usetStatus.setText(signUp.getStatus());
        usetName.setText(signUp.getUserName());
        usetEmail.setText(signUp.getUserId());
        usetDob.setText(signUp.getDob());

        if (signUp.getImageUrl()==null || signUp.getImageUrl().equals("default")) {
            userProfilePic.setImageResource(R.drawable.ic_perm_identity_black_24dp);
        }
        else {
            Picasso.with(this)
                    .load(signUp.getImageUrl())
                    .into(userProfilePic);

            Log.e("setting imgae : ", " --- "+signUp.getImageUrl());
        }

        userVisibilityMiles.setProgress(signUp.getVisibilityMiles());

        if(signUp.getVisibilityMode()==null || signUp.getVisibilityMode().equals(getString(R.string.visibility_mode_visible)))
            visibilityModeVisible.setChecked(true);
        else if(signUp.getVisibilityMode().equals(getString(R.string.visibility_mode_invisible)))
            visibilityModeInVisible.setChecked(true);
        else
            visibilityModeDoNotDisturb.setChecked(true);
    }


    public void getUpdatedUserData() {

        signUp.setStatus(usetStatus.getText().toString());

        signUp.setUserName(usetName.getText().toString());

        signUp.setUserId(usetEmail.getText().toString());

        signUp.setDob(usetDob.getText().toString());

        signUp.setVisibilityMiles(userVisibilityMiles.getProgress());


        if(visibilityModeVisible.isChecked())
            signUp.setVisibilityMode(getString(R.string.visibility_mode_visible));
        else if(visibilityModeInVisible.isChecked())
            signUp.setVisibilityMode(getString(R.string.visibility_mode_invisible));

        else if(visibilityModeDoNotDisturb.isChecked())
            signUp.setVisibilityMode(getString(R.string.visibility_mode_donotdisturb));

    }


    private void getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");
            this.signUp = gson.fromJson(json, SignUp.class);


    }

    public void storeUserObject(SharedPreferences.Editor editor)
    {
        Intent in = getIntent();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        Log.e("string is ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
    }


    @Subscribe
    public void getUserObject(SignUp signUp) {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        this.signUp = signUp;
        setUserData();
        dismissProgressDialog();
        storeUserObject(editor);
    }


    public void serverCallToUpdateUserDetail() {
        startProgressDialog();
        String url = getString(R.string.ip_local)+getString(R.string.upda_user);
        updateUserDetail = new UpdateUserDetail(signUp, url);
        updateUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void setProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog() {
        progressDialog.show();
    }


    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }

    public void passwordALertBox(){


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alertDialogBuilder.setMessage("Enter the password");
        alertDialogBuilder.setTitle("Verify");
        alertDialogBuilder.setView(edittext);
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Log.e("password "," is : "+signUp.getPassword());
                        Log.e("password textbox :  "," is : "+securityOperations.encryptNetworkPassword(edittext.getText().toString()));

                        if(securityOperations.comparePassword(edittext.getText().toString(), signUp.getPassword()))
                            usetStatus.setEnabled(true);
                        else {
                            edittext.setError("Invalid password");
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancle",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        alertDialogBuilder.show();
    }

}

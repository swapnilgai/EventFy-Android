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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.UpdatePassword;
import com.java.eventfy.utils.SecurityOperations;

import org.greenrobot.eventbus.Subscribe;

public class PasswordUpdate extends AppCompatActivity {

    private Button passwordUpdateBtn;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private SecurityOperations securityOperations;
    private SignUp signUp;
    private TextView errorMsg;
    private String passwordTemp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);
        EventBusService.getInstance().register(this);
        userPassword = (EditText) findViewById(R.id.user_password);
        userConfirmPassword = (EditText) findViewById(R.id.user_confirm_password);
        passwordUpdateBtn = (Button) findViewById(R.id.btn_update_user_password);
        errorMsg = (TextView) findViewById(R.id.error_msg);
        errorMsg.setVisibility(View.INVISIBLE);
        setProgressDialog();
        userPassword.setEnabled(false);
        userConfirmPassword.setEnabled(false);

        getUserObject();

        securityOperations = new SecurityOperations();

        passwordUpdateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordUpdateALertBox();
            }
        });

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profileeditmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                passwordALertBox();
                break;
            default:
                break;
        }
        return true;
    }


    public void enablePasswordFields() {

        userPassword.setEnabled(true);
        userConfirmPassword.setEnabled(true);
    }

    public void passwordALertBox() {


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

                        if (securityOperations.comparePassword(edittext.getText().toString(), signUp.getPassword())) {
                                enablePasswordFields();

                        } else {
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

    private void getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    public void storeUserObject()
    {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Intent in = getIntent();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        Log.e("string is ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
    }

    public void updatePassswordServerCall() {
        if(userPassword.getText().toString().equals(userConfirmPassword.getText().toString())) {

            startProgressDialog();
            passwordTemp = userPassword.getText().toString();
            signUp.setPassword(securityOperations.encryptNetworkPassword(userPassword.getText().toString()));
            String url = getString(R.string.ip_local).concat(getString(R.string.update_user_password));
            UpdatePassword updatePassword = new UpdatePassword(signUp, url);
            updatePassword.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
        else{
            // TODO show error message


        }
    }


    public void passwordUpdateALertBox() {


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alertDialogBuilder.setMessage("Would you like to update password? ");
        alertDialogBuilder.setTitle("Update Password");
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        updatePassswordServerCall();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancle",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        errorMsg.setVisibility(View.VISIBLE);
                    }
                });

        alertDialogBuilder.show();
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

    @Subscribe
    public void getUpdatedPassword(SignUp signUp) {
        Log.e("in password signup", ""+signUp.getPassword());
        dismissProgressDialog();
        if(signUp.getViewMessage()==null) {

            Log.e("password signup dismiss", ""+signUp.getPassword());
            this.signUp.setPassword(securityOperations.encryptNetworkPassword(passwordTemp));
            storeUserObject();
            userPassword.setEnabled(false);
            userConfirmPassword.setEnabled(false);

            Toast.makeText(getApplicationContext(), "Password updated successfully",
                    Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getApplicationContext(), "Error, please retry",
                    Toast.LENGTH_LONG).show();
        }
    }
}

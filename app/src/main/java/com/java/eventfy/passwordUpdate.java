package com.java.eventfy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.google.gson.Gson;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.utils.SecurityOperations;

import at.markushi.ui.CircleButton;

public class passwordUpdate extends AppCompatActivity {

    private CircleButton passwordUpdateBtn;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private SecurityOperations securityOperations;
    private SignUp signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        userPassword = (EditText) findViewById(R.id.user_password);
        userConfirmPassword = (EditText) findViewById(R.id.user_confirm_password);
        passwordUpdateBtn = (CircleButton) findViewById(R.id.password_edit_btn);

        securityOperations = new SecurityOperations();

        passwordUpdateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordALertBox();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profileeditmenu, menu);
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

    public void storeUserObject(SharedPreferences.Editor editor)
    {
        Intent in = getIntent();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        Log.e("string is ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
    }


}

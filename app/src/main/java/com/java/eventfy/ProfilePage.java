package com.java.eventfy;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.UpdateUserDetail;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.ImagePicker;
import com.java.eventfy.utils.SecurityOperations;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends AppCompatActivity {

    private EditText usetStatus;
    private EditText usetName;
    private EditText usetEmail;
    private EditText usetDob;
    private SeekBar userVisibilityMiles;
    private RadioGroup userVisibilityMode;
    private SignUp signUp;
    private RadioButton visibilityModeVisible, visibilityModeInVisible, visibilityModeDoNotDisturb;
    private Button saveButton;
    private UpdateUserDetail updateUserDetail;
    private ProgressDialog progressDialog;
    private  LinearLayout linearLayoutStatus;
    private  Bitmap eventImageBM;
    private CircleImageView userProfilePic;
    private static final int PICK_IMAGE_ID = 234;
    private TextView changePasswordLink;
    private Uri dest;
    private CircleButton verifyUserAccount;
    private  SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    private TextView userVisibilityMilesTextView;

    private String buttonClickFlag;
    private SecurityOperations securityOperations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getUserObject();
        setProgressDialog();
        EventBusService.getInstance().register(this);

        userVisibilityMode = (RadioGroup) findViewById(R.id.user_visibility_mode);
        usetStatus = (EditText) findViewById(R.id.user_status);
        usetName = (EditText) findViewById(R.id.user_name);
        usetEmail = (EditText) findViewById(R.id.user_id);
        usetDob = (EditText) findViewById(R.id.user_dob);

        userVisibilityMiles =  (SeekBar) findViewById(R.id.user_visibility_miles);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);
        linearLayoutStatus = (LinearLayout) findViewById(R.id.user_status_linear_layout);
        visibilityModeVisible = (RadioButton) findViewById(R.id.user_visible);
        visibilityModeInVisible = (RadioButton) findViewById(R.id.user_invisible);
        visibilityModeDoNotDisturb = (RadioButton) findViewById(R.id.user_donotdisturb);
        linearLayoutStatus.setEnabled(false);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);

        userVisibilityMilesTextView = (TextView) findViewById(R.id.user_visibility_miles_text_view);

        userProfilePic = (CircleImageView) findViewById(R.id.user_profile_pic);

        securityOperations = new SecurityOperations();

        verifyUserAccount = (CircleButton) findViewById(R.id.verify_user_account_btn);


        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Account");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getUpdatedUserData();
                serverCallToUpdateUserDetail();
            }
        });


        verifyUserAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(signUp.getIsVerified().equals("false"))
                    verifyAccountPopUpBox();
            }
        });

        if(signUp!=null && signUp.getUserId()!=null)
            setUserData();

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(ProfilePage.this, userProfilePic);

                popup.getMenuInflater()
                        .inflate(R.menu.profilepicturemenu, popup.getMenu());
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_profile_pic:
                                //handle menu1 click
                                userProfilePic.setImageResource(R.drawable.user_image);
                                eventImageBM = null;
                                signUp.setImageUrl("default");
                                break;
                            case R.id.replace_profile_pic:
                                //handle menu2 click
                                signUp.setImageUrl(null);

                                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
                                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                                break;
                            case R.id.view_profile_pic:
                                //handle menu2 click

                                ImageViewEntity imageViewEntity = new ImageViewEntity();
                                imageViewEntity.setImageUrl(signUp.getImageUrl());


                                if(eventImageBM!=null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    eventImageBM.compress(CompressFormat.JPEG, 50, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    imageViewEntity.setBitmapByteArray(byteArray);
                                }

                                imageViewEntity.setUserName(signUp.getUserName());

                                Intent intent = new Intent(ProfilePage.this, ImageFullScreenMode.class);
                                intent.putExtra(getString(R.string.image_view_for_fullscreen_mode), imageViewEntity);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfilePage.this, userProfilePic, "profile_pic_transition");
                                    startActivity(intent, transitionActivityOptions.toBundle());
                                }else {
                                    startActivity(intent);
                                }

                               // EventBusService.getInstance().post(imageViewEntity);
                                EventBusService.getInstance().unregister(this);

                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                  popup.show();

            }
        });

        userVisibilityMiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                userVisibilityMilesTextView.setText(String.valueOf(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        changePasswordLink = (TextView) findViewById(R.id.link_chnage_password);


        changePasswordLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordUpdate.class);
                startActivity(intent);
            }
        });

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
        if(signUp.getIsVerified().equals("false")){
            verifyUserAccount.setImageResource(R.drawable.not_verified);
        }
        else{
            verifyUserAccount.setImageResource(R.drawable.verified);
        }

        if(signUp.getVisibilityMiles()<=0)
            userVisibilityMilesTextView.setText("1");
        else
            userVisibilityMilesTextView.setText(String.valueOf(signUp.getVisibilityMiles()));

        if (signUp.getImageUrl()==null || signUp.getImageUrl().equals("default")) {
            userProfilePic.setImageResource(R.drawable.user_image);
        }
        else {

            Picasso.with(getApplicationContext()).load(signUp.getImageUrl())
                    .resize(160, 160)
                    .into(userProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) userProfilePic.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            userProfilePic.setImageDrawable(imageDrawable);
                        }
                        @Override
                        public void onError() {
                            userProfilePic.setImageResource(R.drawable.user_image);
                        }
                    });

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


        //signUp.setPassword(securityOperations.encryptNetworkPassword());
    }


    private void getUserObject() {
        mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");
            this.signUp = gson.fromJson(json, SignUp.class);
    }

    public void storeUserObject()
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

        if(signUp.getViewMessage() == null) {
             mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
             editor = mPrefs.edit();

            if (signUp.getToken() != null)
                this.signUp.setToken(signUp.getToken());

            if(signUp.getIsVerified().equals("false")){
                verifyUserAccount.setImageResource(R.drawable.not_verified);
            }

            this.signUp.setImageUrl(signUp.getImageUrl());
            this.signUp.setUserId(signUp.getUserId());
            this.signUp.setIsFacebook(signUp.getIsFacebook());
            this.signUp.setDob(signUp.getDob());
            this.signUp.setIsVerified(signUp.getIsVerified());
            this.signUp.setStatus(signUp.getStatus());
            this.signUp.setUserId(signUp.getUserId());
            this.signUp.setUserName(signUp.getUserName());
            this.signUp.setVisibilityMiles(signUp.getVisibilityMiles());
            this.signUp.setVisibilityMode(signUp.getVisibilityMode());
            setUserData();
            dismissProgressDialog();
            storeUserObject();

            toastMsg("Profile updated successfully");

        }else{
            toastMsg("Error, please retry");
        }
    }


    public void serverCallToUpdateUserDetail() {
        startProgressDialog();
        String urlForUpdateUserData = getString(R.string.ip_local) + getString(R.string.upda_user);
        if(eventImageBM!=null){
            UploadImage uploadImage = new UploadImage(urlForUpdateUserData, signUp, eventImageBM);
            uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            updateUserDetail = new UpdateUserDetail(signUp, urlForUpdateUserData);
            updateUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public void setProgressDialog() {
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

                        if(securityOperations.comparePassword(edittext.getText().toString(), signUp.getPassword())) {
                                    enableStatusFields();
                                enablePersonalInfoFields();
                        }
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


    public void verifyAccountPopUpBox(){

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            final EditText edittext = new EditText(getApplicationContext());
            edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            alertDialogBuilder.setTitle("Verify Account");
            alertDialogBuilder.setMessage("You'r emil/phone is not verified, please click on verify to continue");
            alertDialogBuilder.setCancelable(false);


            alertDialogBuilder.setPositiveButton("Verify",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            VerifyAccount verifyAccount = new VerifyAccount();
                            verifyAccount.setSignUp(signUp);
                            EventBusService.getInstance().unregister(this);
                            Intent intent = new Intent(ProfilePage.this, VerifySignUp.class);
                            intent.putExtra(getString(R.string.verify_account), verifyAccount);
                            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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


    public void enableStatusFields() {
        usetStatus.setEnabled(true);
    }

    public void enablePersonalInfoFields() {
        usetName.setEnabled(true);
        usetEmail.setEnabled(true);
        usetDob.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_ID) {
            Uri selectedImage = ImagePicker.getImageFromResult(this, resultCode, data);
            dest = beginCrop(selectedImage);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data, dest);
        }
    }

    @Subscribe
    public void getVerifiedAccount(VerifyAccount verifyAccount){
        if(verifyAccount.getViewMsg()!= null && verifyAccount.getViewMsg().equals(getString(R.string.verify_account_fail))){
            toastMsg("Error while verifying account");
        }
        else{
            signUp.setIsVerified("true");
            storeUserObject();
            verifyUserAccount.setImageResource(R.drawable.verified);
        }
    }


    public void toastMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private Uri beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(100, 100).start(this, Crop.REQUEST_CROP);
        return  destination;
    }

    private void handleCrop(int resultCode, Intent result, Uri destination) {


        if (resultCode == RESULT_OK) {

            eventImageBM = decodeBitmap(this, destination, 3);

            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), eventImageBM);
            imageDrawable.setCircular(true);
            imageDrawable.setCornerRadius(Math.max(eventImageBM.getWidth(), eventImageBM.getHeight()) / 2.0f);
            userProfilePic.setImageDrawable(imageDrawable);

            //   mImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            toastMsg(Crop.getError(result).getMessage());

        }
    }


    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        Options options = new Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);


        return actuallyUsableBitmap;
    }
    // Crop image end

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
             EventBusService.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // EventBusService.getInstance().unregister(this);
    }
}

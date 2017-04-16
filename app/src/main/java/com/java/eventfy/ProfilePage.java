package com.java.eventfy;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.UpdateAccount;
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.UpdateUserDetail;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.java.eventfy.utils.ImagePicker;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.TimeZone;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.eventfy.SignUpActivity.DATEPICKER_TAG;

public class ProfilePage extends AppCompatActivity implements OnDateSetListener {

    private EditText userStatus;
    private EditText userName;
    private EditText userEmail;
    private EditText userDob;
    private SeekBar userVisibilityMiles;
    private RadioGroup userVisibilityMode;
    private SignUp signUp;
    private RadioButton visibilityModeVisible, visibilityModeInVisible, visibilityModeDoNotDisturb;
    private Button saveButton;
    private UpdateUserDetail updateUserDetail;
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
    private RobotoTextView dobErrorMsg;
    private  ObjectAnimator animator;
    private LinearLayout updateProfileLinearLayout;
    private ImageView loadingImage;
    private String dobString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getUserObject();
        EventBusService.getInstance().register(this);

        userVisibilityMode = (RadioGroup) findViewById(R.id.user_visibility_mode);
        userStatus = (EditText) findViewById(R.id.user_status);
        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_id);
        userDob = (EditText) findViewById(R.id.user_dob);

        userVisibilityMiles =  (SeekBar) findViewById(R.id.user_visibility_miles);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);
        linearLayoutStatus = (LinearLayout) findViewById(R.id.user_status_linear_layout);
        visibilityModeVisible = (RadioButton) findViewById(R.id.user_visible);
        visibilityModeInVisible = (RadioButton) findViewById(R.id.user_invisible);
        visibilityModeDoNotDisturb = (RadioButton) findViewById(R.id.user_donotdisturb);
        linearLayoutStatus.setEnabled(false);
        saveButton = (Button) findViewById(R.id.btn_save_user_profile);
        dobErrorMsg = (RobotoTextView) findViewById(R.id.link_dob_error);

        userVisibilityMilesTextView = (TextView) findViewById(R.id.user_visibility_miles_text_view);

        userProfilePic = (CircleImageView) findViewById(R.id.user_profile_pic);

        verifyUserAccount = (CircleButton) findViewById(R.id.verify_user_account_btn);

        updateProfileLinearLayout = (LinearLayout) findViewById(R.id.updating_profile_linear_layout);

        loadingImage = (ImageView) findViewById(R.id.loadingImage);

        changePasswordLink = (TextView) findViewById(R.id.link_chnage_password);

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(ProfilePage.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);


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

                boolean isUser13plus = false;
                if(dobString!=null)
                    isUser13plus = checkUSerIs13Plus(dobString);

                userDob.setError(null);
                if(isUser13plus) {
                    dobErrorMsg.setVisibility(View.VISIBLE);
                    userDob.setError("Minimum age to SignUp is 13");
                }
                else if(validate()){
                    getUpdatedUserData();
                    serverCallToUpdateUserDetail();
                }
            }
        });


        verifyUserAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(signUp.getFacebookId()==null && signUp.getIsVerified().equals("false"))
                    verifyAccountPopUpBox();
            }
        });

        if(signUp!=null && signUp.getUserId()!=null)
            setUserData(signUp);

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

        changePasswordLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordUpdate.class);
                startActivity(intent);
            }
        });

        userDob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1910, calendar.get(Calendar.YEAR) - 13);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
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

    public void setUserData(SignUp signUp) {
        userStatus.setText(signUp.getStatus());
        userName.setText(signUp.getUserName());
        userEmail.setText(signUp.getUserId());
        userDob.setText(DateTimeStringOperations.getInstance().getDateString(signUp.getDob()));
        if(signUp.getIsFacebook().equals("false") && signUp.getFacebookId()==null && signUp.getIsVerified().equals("false")){
            verifyUserAccount.setImageResource(R.drawable.not_verified);
        }
        else{
            verifyUserAccount.setImageResource(R.drawable.verified);
        }

        if(signUp.getFacebookId()!=null){
            changePasswordLink.setVisibility(View.GONE);
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
                            imageBitmap.recycle();
                            imageBitmap=null;
                            System.gc();

                        }
                        @Override
                        public void onError() {
                            userProfilePic.setImageResource(R.drawable.user_image);
                        }
                    });
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

        signUp.setStatus(userStatus.getText().toString());

        signUp.setUserName(userName.getText().toString());

        signUp.setUserId(userEmail.getText().toString());

        if(dobString!=null)
            signUp.setDob(dobString);

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

    public void storeUserObject() {
        Intent in = getIntent();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        Log.e("string is ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
    }


    @Subscribe
    public void getUserObject(UpdateAccount updateAccount) {
        SignUp signUp = updateAccount.getSignUp();
        dismissProgressDialog();
        if(signUp.getViewMessage().equals(R.string.user_account_update_success)) {
            if(signUp.getIsVerified().equals("false")){
                verifyUserAccount.setImageResource(R.drawable.not_verified);
            }
            else{
                verifyUserAccount.setImageResource(R.drawable.verified);
            }
            setUserData(signUp);
            toastMsg("Updated successfully");

        }else if(signUp.getViewMessage().equals(R.string.user_account_update_fail)){
            toastMsg("Error, please check user detail");
        }
        else if(signUp.getViewMessage().equals(R.string.user_account_update_server_error)){
            toastMsg("Server error, please try again later");
        }
    }


    public void serverCallToUpdateUserDetail() {
        startProgressDialog();
        dobErrorMsg.setVisibility(View.GONE);
        String urlForUpdateUserData = getString(R.string.ip_local) + getString(R.string.upda_user);
        if(eventImageBM!=null){
            UploadImage uploadImage = new UploadImage(urlForUpdateUserData, signUp, eventImageBM);
            uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            updateUserDetail = new UpdateUserDetail(signUp, urlForUpdateUserData, getApplicationContext());
            updateUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void startProgressDialog() {
        saveButton.setVisibility(View.GONE);
        updateProfileLinearLayout.setVisibility(View.VISIBLE);
        animator = ObjectAnimator.ofFloat(loadingImage, "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();
    }


    public void dismissProgressDialog() {
        animator.cancel();
        updateProfileLinearLayout.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);


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
                            verifyAccount.setActivityName(getString(R.string.activity_ProfilePage));
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
        //getMenuInflater().inflate(R.menu.profileeditmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                break;
            default:
                break;
        }
        return true;
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
            verifyUserAccount.setImageResource(R.drawable.verified);
            toastMsg("Profile updated successfully");
        }
        dobErrorMsg.setVisibility(View.GONE);
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

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 300, 200);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        return actuallyUsableBitmap;
    }
    // Crop image end


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

    public boolean validate() {
        boolean valid = true;

        signUp.setStatus(userStatus.getText().toString());

        signUp.setUserName(userName.getText().toString());

        signUp.setUserId(userEmail.getText().toString());

        if(dobString!=null)
            signUp.setDob(dobString);

        signUp.setVisibilityMiles(userVisibilityMiles.getProgress());

        if (userStatus.getText().toString().isEmpty()) {
            userStatus.setError("Status cant be empty");
            valid = false;
        } else {
            userStatus.setError(null);
        }
        if (userDob.getText().toString().isEmpty()){

            if(userDob.getText().toString().isEmpty()){
                userDob.setText("Date of birth cant be empty");
            }

            dobErrorMsg.setVisibility(View.VISIBLE);
            userDob.setError("Minimum age to SignUp is 13");
            valid = false;
        }
        else {
            userDob.setError(null);
            dobErrorMsg.setVisibility(View.GONE);
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString()).matches()) {
            userEmail.setError(null);

        } else {
            userEmail.setError("Enter a valid email address");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        dobString = year + "-" + month + "-" + day;

        userDob.setText(DateTimeStringOperations.getInstance().getDateString(dobString));

    }


    public boolean checkUSerIs13Plus(String userDob){


        org.joda.time.DateTime dateTimeNow = new org.joda.time.DateTime(DateTimeZone.forID(TimeZone.getDefault().getID()));
        dateTimeNow = dateTimeNow.plusYears(-13);
        org.joda.time.DateTime userDobDateTime = convertStringToDateTime(userDob, TimeZone.getDefault().getID());

        boolean b = true;
        if(dateTimeNow.isBefore(userDobDateTime)){
            b= true;
        }
        else
            b = false;

        return b;
    }
    public DateTime convertStringToDateTime(String date, String timeZone){
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("MM-dd-yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
                DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").getParser(),
                DateTimeFormat.forPattern("yyyy/MM/dd HH:mm").getParser(),
                DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
                DateTimeFormat.forPattern("MM/dd/yyyy").getParser()
        };

        DateTime out = null;
        try {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(null, parsers)
                .toFormatter()
                .withZone(DateTimeZone.forID(timeZone));
        out = formatter.parseDateTime(date);

        }catch (Exception e){
            toastMsg("Please enter valid date of birth");
            userDob.setText(DateTimeStringOperations.getInstance().getDateString(signUp.getDob()));
        }
        return out;

    }


}

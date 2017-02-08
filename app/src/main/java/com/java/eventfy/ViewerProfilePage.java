package com.java.eventfy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewerProfilePage extends AppCompatActivity {
    private TextView usetStatus;
    private TextView usetName;
    private RadioGroup userVisibilityMode;
    private SignUp signUp;
    private RadioButton visibilityModeVisible;
    private ImageView userProfilePic;
    private static final int PICK_IMAGE_ID = 234;
    private Uri dest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_profile_page);

        userVisibilityMode = (RadioGroup) findViewById(R.id.user_visibility_mode);
        usetStatus = (TextView) findViewById(R.id.user_status);
        usetName = (TextView) findViewById(R.id.user_name);

        userProfilePic = (ImageView) findViewById(R.id.user_profile_pic);

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
        Intent intent = getIntent();
        signUp = (SignUp) intent.getSerializableExtra(getString(R.string.signup_object_viewe_profile));


        if (signUp != null && signUp.getUserId() != null)
            setUserData();

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //handle menu2 click

                ImageViewEntity imageViewEntity = new ImageViewEntity();
                imageViewEntity.setImageUrl(signUp.getImageUrl());

                imageViewEntity.setUserName(signUp.getUserName());

                Intent intent = new Intent(ViewerProfilePage.this, ImageFullScreenMode.class);
                intent.putExtra(getString(R.string.image_view_for_fullscreen_mode), imageViewEntity);

                startActivity(intent);


            }
        });


    }


    public void getVisibilityMode() {


        userVisibilityMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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

        if (signUp.getImageUrl() == null || signUp.getImageUrl().equals("default")) {
            userProfilePic.setImageResource(R.drawable.ic_perm_identity_black_24dp);
        } else {
//            Picasso.with(this)
//                    .load(signUp.getImageUrl())
//                    .transform(new RoundedCornersTransform())
//                    .into(userProfilePic);


            Picasso.with(getApplicationContext()).load(signUp.getImageUrl())
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
                            userProfilePic.setImageResource(R.drawable.circular_user_image);
                        }
                    });

        }

   //     userVisibilityMiles.setProgress(signUp.getVisibilityMiles());

//        if(signUp.getVisibilityMode()==null || signUp.getVisibilityMode().equals(getString(R.string.visibility_mode_visible)))
//            vis.setChecked(true);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

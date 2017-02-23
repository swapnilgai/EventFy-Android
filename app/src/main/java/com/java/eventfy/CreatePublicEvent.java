package com.java.eventfy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment1;
import com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment2;
import com.java.eventfy.utils.CustomViewPager;
import com.java.eventfy.utils.ImagePicker;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CreatePublicEvent extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private FloatingActionButton addImage;
    private String category;
    private View view;
    private static final int PICK_IMAGE_ID = 234;
    private Bitmap eventImageBM;
    private ImageView eventImageIV;
    private Uri dest;
    private Events event;
    private SignUp signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_public_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        category = getIntent().getExtras().getString(getString(R.string.create_event_category));

        event = (Events) getIntent().getSerializableExtra(getString(R.string.event_to_edit_eventinfo));

        signUp = getUserObject();


        if(event == null && signUp.getIsVerified().equals("false")){
            // Call from create event
               setErrorMessageToVerifyAccount();
        }else {
            mapCofigData();
        }
    }

    public void setErrorMessageToVerifyAccount(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alertDialogBuilder.setTitle("Verify Account");
        alertDialogBuilder.setMessage("Please verify you'r emil/phone to create event");
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setPositiveButton("Verify",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        VerifyAccount verifyAccount = new VerifyAccount();
                        verifyAccount.setSignUp(signUp);
                        EventBusService.getInstance().unregister(this);
                        Intent intent = new Intent(CreatePublicEvent.this, VerifySignUp.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(getString(R.string.verify_account), verifyAccount);
                        finish();
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancle",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        alertDialogBuilder.show();
    }


    public void mapCofigData(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        eventImageIV = (ImageView) findViewById(R.id.event_image);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        addImage = (FloatingActionButton) findViewById(R.id.add_image);

        if(event!=null) {
            category = event.getEventType();
            if(!event.getEventImageUrl().equals("default"))
                Picasso.with(this)
                        .load(event.getEventImageUrl())
                        .placeholder(R.drawable.logo)
                        .into(eventImageIV);
        }

        tabLayout.setupWithViewPager(viewPager);

        addImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(CreatePublicEvent.this, addImage);
                //inflating menu from xml resource

//                    popup.inflate(R.menu.profilepicturemenu);

                popup.getMenuInflater()
                        .inflate(R.menu.profilepicturemenu, popup.getMenu());
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_profile_pic:
                                //handle menu1 click
                                eventImageIV.setImageResource(R.drawable.logo);
                                eventImageBM = null;
                                // sending fake object to avoid over writing of image after removal
                                double d = 2.0;
                                EventBusService.getInstance().post(d);
                                break;
                            case R.id.replace_profile_pic:
                                //handle menu2 click
                                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
                                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                                break;
                            case R.id.view_profile_pic:
                                //handle menu2 click

                                ImageViewEntity imageViewEntity = new ImageViewEntity();
                                if(event!=null && !event.getEventImageUrl().equals("default")) {
                                    imageViewEntity.setImageUrl(event.getEventImageUrl());
                                }

                                else if(eventImageBM!=null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    eventImageBM.compress(CompressFormat.JPEG, 50, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    imageViewEntity.setBitmapByteArray(byteArray);
                                }


                                Intent intent = new Intent(CreatePublicEvent.this, ImageFullScreenMode.class);
                                intent.putExtra(getString(R.string.image_view_for_fullscreen_mode), imageViewEntity);

                                startActivity(intent);


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



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });

        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        CreateEventFragment1 createEventFragment1 = new CreateEventFragment1();
        CreateEventFragment2 createEventFragment2 = new CreateEventFragment2();

        Log.e("event to edit : ", ""+event);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.event_type_value), category);
        bundle.putSerializable(getString(R.string.event_to_edit_eventinfo), event);
        createEventFragment1.setArguments(bundle);


        adapter.addFrag(createEventFragment1, "Information");

        Log.e("edit option : ", " 0000) "+category);
        if(category!=null && category.equals(getString(R.string.create_event_category_private))) {
            adapter.addFrag(createEventFragment2, "Invite");

        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private Uri beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(150, 100).start(this, Crop.REQUEST_CROP);
        return  destination;
    }

    private void handleCrop(int resultCode, Intent result, Uri destination) {


        if (resultCode == RESULT_OK) {

            eventImageBM = decodeBitmap(this, destination, 3);
            eventImageIV.setImageBitmap(eventImageBM);
            EventBusService.getInstance().post(eventImageBM);
            //   mImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Subscribe
    public void getCreatedEventFromServer(Events event) {
        finish();
    }

    private SignUp getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");
        return  gson.fromJson(json, SignUp.class);
    }

}





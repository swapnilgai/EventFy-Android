package com.java.eventfy;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment1;
import com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment2;
import com.java.eventfy.utils.CustomViewPager;
import com.java.eventfy.utils.ImagePicker;
import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.Subscribe;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_public_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        category = getIntent().getExtras().getString(getString(R.string.create_event_category));

        event = (Events) getIntent().getSerializableExtra(getString(R.string.event_to_edit_eventinfo));

        Log.e("cate bef "," 000000 "+category);

        if(event!=null) {
            category = event.getEventType();
        }


        Log.e("cate aft "," 000000 "+category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        eventImageIV = (ImageView) findViewById(R.id.event_image);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        addImage = (FloatingActionButton) findViewById(R.id.add_image);

        tabLayout.setupWithViewPager(viewPager);



        addImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
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
    public void getCreatedEventFromServer(Events event)
    {
        finish();
//        if(event.getViewMessage().equals(R.string.edited)) {
//           finish();
//        }
    }

}



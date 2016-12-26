package com.java.eventfy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.EventBus.EventBusService;
import com.squareup.picasso.Picasso;

public class ImageFullScreenMode extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private  Toolbar myToolbar;
    private ImageViewEntity imageViewObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen_mode);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(0xFFFFFFFF);

        imageView = (ImageView) findViewById(R.id.item_image);
        textView = (TextView) findViewById(R.id.item_text);

        Intent intent = getIntent();
        imageViewObj = (ImageViewEntity) intent.getSerializableExtra(getString(R.string.image_view_for_fullscreen_mode));
        setImageDataObject();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                EventBusService.getInstance().unregister(this);
                onBackPressed();
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myToolbar.getVisibility()!= View.INVISIBLE) {
                    myToolbar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                }
                else{
                    myToolbar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public void setImageDataObject() {
        if(imageViewObj.getBitmapByteArray()==null)
        {
            Picasso.with(getApplicationContext()).
                    load(imageViewObj.getImageUrl())
                    .fit()
                    .into(imageView);

        }
        else if(imageViewObj.getBitmapByteArray()!=null && imageViewObj.getBitmapByteArray().equals("default")){
            Bitmap imageBitmap =  BitmapFactory.decodeByteArray(imageViewObj.getBitmapByteArray(), 0, imageViewObj.getBitmapByteArray().length);

            imageView.setImageBitmap(imageBitmap);
        }else {
            imageView.setImageResource(R.drawable.circular_user_image);
        }

        Log.e("image view  obj : ", ""+imageViewObj.getTextMessage());

        if(imageViewObj.getTextMessage()!=null)
            textView.setText(imageViewObj.getTextMessage());
        else
            textView.setText("");

        Log.e("image title : ", ""+imageViewObj.getUserName());

        myToolbar.setTitle(imageViewObj.getUserName());

    }

}

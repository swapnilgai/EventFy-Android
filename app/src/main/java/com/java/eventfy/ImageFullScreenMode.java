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
import com.java.eventfy.utils.RoundedCornersTransformCommentAuthor;
import com.squareup.picasso.Picasso;

public class ImageFullScreenMode extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Toolbar myToolbar;
    private ImageViewEntity imageViewObj;
    private ImageView userImage;
    private TextView userName;
    private TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen_mode);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Profile picture");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageView = (ImageView) findViewById(R.id.item_image);
        textView = (TextView) findViewById(R.id.item_text);
        userImage = (ImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        date = (TextView) findViewById(R.id.date_text);

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
                    date.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.INVISIBLE);
                    userName.setVisibility(View.INVISIBLE);

                }
                else{
                    myToolbar.setVisibility(View.VISIBLE);

                    if(imageViewObj.getTextMessage()!=null)
                        textView.setVisibility(View.VISIBLE);

                    if(imageViewObj.getDate()!=null)
                        date.setVisibility(View.VISIBLE);

                    if(imageViewObj.getUserImageUrl()!=null)
                        userImage.setVisibility(View.VISIBLE);

                    if(imageViewObj.getUserName()!=null)
                        userName.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public void setImageDataObject() {
        Log.e("out condition 1  : ", ""+imageViewObj.getBitmapByteArray());
        Log.e("out condition 2  : ", ""+imageViewObj.getUserName());


        if(imageViewObj.getBitmapByteArray()==null && imageViewObj.getImageUrl()!=null && !imageViewObj.getImageUrl().equals("default") )
        {
            Picasso.with(getApplicationContext()).
                    load(imageViewObj.getImageUrl())
                    .fit()
                    .into(imageView);

        }
        else if(imageViewObj.getBitmapByteArray()!=null){
            Bitmap imageBitmap =  BitmapFactory.decodeByteArray(imageViewObj.getBitmapByteArray(), 0, imageViewObj.getBitmapByteArray().length);

            imageView.setImageBitmap(imageBitmap);
        }
        else if(imageViewObj.getBitmapByteArray()==null && imageViewObj.getUserName()!=null && imageViewObj.getImageUrl().equals("default")) {
            imageView.setImageResource(R.drawable.logo);
        }
        else if(imageViewObj.getBitmapByteArray() == null && imageViewObj.getUserName()==null){
            Log.e("in condition :  : ", ""+imageViewObj.getUserName());

            imageView.setImageResource(R.drawable.logo);
        }
        else {
            imageView.setImageResource(R.drawable.circular_user_image);
        }

        Log.e("image view  obj : ", ""+imageViewObj.getTextMessage());

        if(imageViewObj.getTextMessage()!=null)
            textView.setText(imageViewObj.getTextMessage());
        else
            textView.setVisibility(View.GONE);


        if(imageViewObj.getUserImageUrl()!=null)

            Picasso.with(getApplicationContext()).
                load(imageViewObj.getUserImageUrl())
                .fit()
                .transform(new RoundedCornersTransformCommentAuthor())
                .into(userImage);
        else
            userImage.setVisibility(View.GONE);

        if(imageViewObj.getUserName()!=null)
            userName.setText(imageViewObj.getUserName());
        else
            userName.setVisibility(View.GONE);

        if(imageViewObj.getDate()!=null)
            date.setText(imageViewObj.getDate());
        else
            date.setVisibility(View.GONE);


        myToolbar.setTitle(imageViewObj.getUserName());

    }

}

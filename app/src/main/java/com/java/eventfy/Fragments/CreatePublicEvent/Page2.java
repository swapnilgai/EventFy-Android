package com.java.eventfy.Fragments.CreatePublicEvent;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.java.eventfy.Entity.createEvent.page2;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.utils.ImagePicker;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Page2 extends Fragment {
    private page2 page2;
    private ImageView mImageView;
    private Spinner eventType;
    private EditText eventDescription;
    private Button next;
    private Uri dest = null;
    private View getImage;
    private Bitmap bm;
    private CheckBox eventVolatile;
    private static final int PICK_IMAGE_ID = 234;

    public Page2() {
        // Required empty public constructor
        page2 = new page2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.fragment_page2, container, false);

        eventType = (Spinner) view.findViewById(R.id.public_event_type);
        eventDescription = (EditText) view.findViewById(R.id.public_event_description);
        next = (Button) view.findViewById(R.id.public_public_event_page2_next);
        mImageView = (ImageView) view.findViewById(R.id.eventImage);
        eventVolatile = (CheckBox) view.findViewById(R.id.public_events_volatile);

        getImage = (FloatingActionButton) view.findViewById(R.id.get_image);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

//                page2.setEventType(eventType.getSelectedItem().toString());

                page2.setEventDescription(eventDescription.getText().toString());

                EventBusService.getInstance().post(page2);
            }
        });

        eventVolatile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

               }
        });

        getImage = view.findViewById(R.id.get_image);

        getImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(view.getContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        return view;
    }

    public void click() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_ID) {
            Uri selectedImage = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
            dest = beginCrop(selectedImage);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data, dest);
        }

    }

    private Uri beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getContext(), Page2.this, Crop.REQUEST_CROP);
        return  destination;
    }

    private void handleCrop(int resultCode, Intent result, Uri destination) {


        if (resultCode == getActivity().RESULT_OK) {
            Log.e("crop : ", "" + getActivity().getCacheDir());

            bm = decodeBitmap(getActivity(), destination, 3);
            mImageView.setImageBitmap(bm);
            page2.setBm(bm);

            //   mImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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

        Log.d("img : ", options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
    }
    // Crop image end
}

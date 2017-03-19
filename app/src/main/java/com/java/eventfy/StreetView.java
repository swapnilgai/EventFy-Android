package com.java.eventfy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.java.eventfy.Entity.Events;

public class StreetView extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private Events event;
    private Toolbar toolbar;
    private StreetViewPanorama streetViewPanorama;
    private boolean flagInDistance;
    private TextView streetViewTextView;
    private TextView viewMessage;
    int count= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strwwt_view);

        flagInDistance = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        streetViewTextView = (TextView) findViewById(R.id.no_street_view_text_view);
        viewMessage = (TextView) findViewById(R.id.view_message);

        streetViewTextView.setVisibility(View.GONE);
        viewMessage.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });



        this.setFinishOnTouchOutside(true);
        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra(getString(R.string.event_object_for_street_view));

//        StreetViewPanoramaFragment streetViewPanoramaFragment =
//                (StreetViewPanoramaFragment) getFragmentManager()
//                        .findFragmentById(R.id.streetviewpanorama);

        StreetViewPanoramaView streetViewPanoramaView = (StreetViewPanoramaView) findViewById(R.id.steet_view_panorama);

        streetViewPanoramaView.onCreate(savedInstanceState);
        streetViewPanoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {

              //  panorama.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                streetViewPanorama = panorama;
                setLocation();

            }
        });

        streetViewPanoramaView.getStreetViewPanoramaAsync(this);
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;
        //  Intent intent = new Intent(this, YouTubeMediaPlayActivity.class);
       // startActivity(intent);
    }

    public void setLocation(){
        streetViewPanorama.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()));

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                    // location is present
                } else {
                   if(!flagInDistance)
                        setLocation(10000);
                    // location not available
                    else
                       showErrorMessage();

                }
            }
        });
    }

    public void setLocation(int distance){
        viewMessage.setVisibility(View.VISIBLE);
        streetViewPanorama.setPosition(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()), distance);
        flagInDistance= true;
    }

    public void showErrorMessage(){
        viewMessage.setVisibility(View.GONE);
        streetViewTextView.setVisibility(View.VISIBLE);
    }

}

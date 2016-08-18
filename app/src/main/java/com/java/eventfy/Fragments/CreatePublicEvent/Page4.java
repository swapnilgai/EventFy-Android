package com.java.eventfy.Fragments.CreatePublicEvent;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.createEvent.page1;
import com.java.eventfy.Entity.createEvent.page2;
import com.java.eventfy.Entity.createEvent.page3;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.CreatePublicEvent;
import com.java.eventfy.asyncCalls.UploadImage;

import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class Page4 extends Fragment {
    private page1 page1;
    private page2 page2;
    private page3 page3;
    private Events event;
    private UploadImage uploadImage;
    private Button create;

    public Page4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("createing page 4 ", "*********");

        // Inflate the layout for this fragment
        EventBusService.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_page4, container, false);

        create = (Button) view.findViewById(R.id.public_public_event_create);

        create.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity;
                generateEventObject();
                uploadImage = new UploadImage(event, page2.getBm());
                uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        return view;
    }

    @Subscribe
    public void createEventToServer(String result)
    {
      //  Log.e("event url", event.getEventImageUrl());
        if(result.equals("CreateEvent")) {
            String url = getResources().getString(R.string.ip_local) + getResources().getString(R.string.add_event);
            CreatePublicEvent createPublicEvent = new CreatePublicEvent(url, event);
            createPublicEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void uploadImage()
    {
            UploadImage uploadImage = new UploadImage(event, page2.getBm());
            uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getPage1(page1 p1)
    {
        page1 = p1;
        Log.e("page 1 : ", ""+page1.getEventName());
    }

    @Subscribe
    public void getPage2(page2 p2)
    {
        Log.e("in page2 ", ""+p2.getBm().getByteCount());
        page2 = p2;
    }

    @Subscribe
    public void getPage3(page3 p3)
    {
        page3 = p3;
    }


    public void generateEventObject()
    {
        Log.e("longi: ", ""+page3.getLongitude());
        Log.e("lati: ", ""+page3.getLatitude());

        event = new Events();
        //page 1
        event.setEventName(page1.getEventName());
        event.setEventDateFrom(page1.getEventDateFrom());
        event.setEventDateTo(page1.getEventDateTo());
        event.setEventTimeFrom(page1.getEventTimeFrom());
        event.setEventTimeTo(page1.getEventTimeTo());

        //todo page 2 set bitmap is pendig .. perform it before creating event

        event.setEventDescription(page2.getEventDescription());
        event.setEventType(page2.getEventType());

        //page 3
        event.setEventType(page3.getEventType());
        event.setEventVisiblityMile(page3.getEventVisiblity());
        event.setEventLocationLatitude(page3.getLatitude());
        event.setEventLocationLongitude(page3.getLongitude());

    }
}

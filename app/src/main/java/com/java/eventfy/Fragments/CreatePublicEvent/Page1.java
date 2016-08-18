package com.java.eventfy.Fragments.CreatePublicEvent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.java.eventfy.Entity.createEvent.page1;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class Page1 extends Fragment implements OnDateSetListener, OnTimeSetListener {

    private EditText eventName;
    private EditText eventDateFrom;
    private EditText eventDateTo;
    private EditText eventTimeFrom;
    private EditText eventTimeTo;
    private Button next;
    private page1 page1;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_FROM_TAG = "timepicker";
    public static final String TIMEPICKER_TO_TAG = "timepicker";
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;
    private TimePickerDialog timePickerDialogFrom;
    private TimePickerDialog timePickerDialogTo;
    private View timePickerView;

    public Page1() {
        // Required empty public constructor
        page1 = new page1();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_page1, container, false);

        eventName = (EditText) view.findViewById(R.id.event_name);
        eventDateFrom = (EditText) view.findViewById(R.id.public_event_date_from);
        eventDateTo = (EditText) view.findViewById(R.id.public_event_date_to);
        eventTimeFrom = (EditText) view.findViewById(R.id.public_event_time_from);
        eventTimeTo = (EditText) view.findViewById(R.id.public_event_time_to);
        next = (Button) view.findViewById(R.id.public_public_event_page1_next);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialogFrom = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogFrom = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
        datePickerDialogTo = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogTo = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                page1.setEventName(eventName.getText().toString());
                page1.setEventDateFrom(eventDateFrom.getText().toString());
                page1.setEventDateTo(eventDateTo.getText().toString());
                page1.setEventTimeFrom(eventTimeFrom.getText().toString());
                page1.setEventTimeTo(eventTimeTo.getText().toString());

                Log.e("origibal page1 : ", ""+page1);
                EventBusService.getInstance().post(page1);
            }
        });

        eventDateFrom.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialogFrom.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 10);
                datePickerDialogFrom.setFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                datePickerDialogFrom.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialogFrom.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
            }

        });

        eventDateTo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialogTo.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 10);
                datePickerDialogTo.setFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                datePickerDialogTo.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialogTo.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
            }

        });

        eventTimeFrom.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                timePickerView = v;
                timePickerDialogFrom.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
                timePickerDialogFrom.show(getFragmentManager(), TIMEPICKER_FROM_TAG);
            }

        });

        eventTimeTo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                timePickerView = v;
                Log.e("timepickerto :  ", ""+timePickerDialogTo);
                timePickerDialogTo.setVibrate(isVibrate());
                timePickerDialogTo.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
                timePickerDialogTo.show(getFragmentManager(), TIMEPICKER_TO_TAG);

            }

        });

        return view;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        if(datePickerDialog.equals(datePickerDialogTo))
            eventDateTo.setText(year + "-" + month + "-" + day);
        else
            eventDateFrom.setText(year + "-" + month + "-" + day);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = "" + hourString + ":" + minuteString + "";

        if(timePickerView.getId() == R.id.public_event_time_from)
            eventTimeFrom.setText(time);
        else
            eventTimeTo.setText(time);

    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    private boolean isVibrate() {
        return false;
    }
}

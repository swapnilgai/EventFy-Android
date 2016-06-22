package com.java.eventfy.Fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.customLibraries.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Remot extends Fragment {
    MainRecyclerAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private FloatingActionButton fragment_switch_button_remot;
    private FloatingActionButton search_place_button_remot;
    private FragmentTransaction transaction_remot;
    private FragmentManager manager_remot;
    private Fragment remot_map;
    private static final String context_id = "22";

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    static String TAG = "Search bar called";

    public Remot() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_remot, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_remot);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_remot);

        adapter = new MainRecyclerAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetNearbyEvent getNearbyEventForTab1 = new GetNearbyEvent();
                getNearbyEventForTab1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        fragment_switch_button_remot  = (FloatingActionButton) view.findViewById(R.id.fragment_switch_button_remot);
        fragment_switch_button_remot.setImageResource(R.drawable.ic_near_me_white_24dp);

        search_place_button_remot= (FloatingActionButton) view.findViewById(R.id.search_place_button_remot);

        manager_remot = getActivity().getSupportFragmentManager();
        transaction_remot = manager_remot.beginTransaction();
        view.setId(Integer.parseInt(context_id));

        remot_map = new Remot_Map();
        transaction_remot.add(Integer.parseInt(context_id), remot_map, "remot_map");
        transaction_remot.hide(remot_map);
        transaction_remot.commit();

        EventBusService.getInstance().register(this);

        fragment_switch_button_remot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(remot_map.isHidden()) {
                    transaction_remot = manager_remot.beginTransaction();
                    transaction_remot.show(remot_map);
                    transaction_remot.commit();
                    fragment_switch_button_remot.setImageResource(R.drawable.ic_near_me_white_24dp);
                }
                else{
                    transaction_remot = manager_remot.beginTransaction();
                    transaction_remot.hide(remot_map);
                    transaction_remot.commit();
                    fragment_switch_button_remot.setImageResource(R.drawable.ic_map_white_24dp);
                }
            }
        });


        search_place_button_remot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                openAutocompleteActivity();
            }
        });

        return view;
    }


    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        Log.e("in receiver remot : ", ""+eventsList.size());
        bindAdapter(adapter, eventsList);
    }

    @Subscribe
    public void setFlag(String flag)
    {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
      //  EventBusService.getInstance().unregister(this);
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
        swipeRefreshLayout.setRefreshing(false);
        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }


    //search autocomplete

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place Selected: " + place.getName());


                // Display attributions if required.

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
}



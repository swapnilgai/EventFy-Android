package com.java.eventfy.Fragments;


import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.GetNearbyEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class Place_Autocomplete_Search extends Fragment implements PlaceSelectionListener {

    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;
    public String TAG = "Search Fragment";
    private PlaceAutocompleteFragment autocompleteFragment;
    private GetNearbyEvent getNearbyEvent;

    public Place_Autocomplete_Search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_place__autocomplete__search, container, false);

        // Retrieve the PlaceAutocompleteFragment.
        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        // Retrieve the TextViews that will display details about the selected place.
        mPlaceDetailsText = (TextView) view.findViewById(R.id.place_details);
        mPlaceAttribution = (TextView) view.findViewById(R.id.place_attribution);
        return view;
    }

    @Override
    public void onPlaceSelected(Place place) {

        Log.i(TAG, "Place Selected: " + place.getName());
        Log.i(TAG, "Place Selected: " + place.getLatLng());
        EventBusService.getInstance().post(place);
        // Format the returned place's details and display them in the TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));
        CharSequence attributions = place.getAttributions();

        //Broadcast message to remot map


        getRemotEventServerCall(place);

        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            mPlaceAttribution.setText("");
        }

    }

    @Override
    public void onError(Status status) {

        Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    // ****** ASYNC CALL
    private void getRemotEventServerCall(Place place){
        Location location = new Location();
        location.setLatitude(place.getLatLng().latitude);
        location.setLongitude(place.getLatLng().longitude);
        location.setUserId("temp");
        //setting url
        String url = getResources().getString(R.string.ip_local) + getResources().getString(R.string.get_nearby_event);
      //  getNearbyEvent = new GetNearbyEvent(url, location, getResources().getString(R.string.remot_flag));
      //  getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

package com.example.iliuxa.balinasoft.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iliuxa.balinasoft.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentContacts extends Fragment implements OnMapReadyCallback {
    private TextView telNumbers;
    private MapFragment mapFragment;
    private GoogleMap map;
    private final double latitude = 53.928733;
    private final double longitude = 27.587202;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        getActivity().setTitle(getString(R.string.contactsTitle));
        initElements(v);
        initMap();
        return v;
    }

    private void initElements(View v) {
        telNumbers = (TextView) v.findViewById(R.id.numbersText);
        String temp = "";
        String[] telephoneNumbers = getResources().getStringArray(R.array.telNumbers);
        for (String telephoneNumber : telephoneNumbers) temp += telephoneNumber + "\n";
        telNumbers.setText(temp);
    }

    private void initMap() {
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Наш Ресторан"));
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getMyLocation();
    }


}

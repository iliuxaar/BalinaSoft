package com.example.iliuxa.balinasoft.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iliuxa.balinasoft.Data.DataBase;
import com.example.iliuxa.balinasoft.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class DishFragment extends Fragment {

    private DataBase dataBase;
    private TextView name;
    private TextView price;
    private TextView description;
    private TextView weight;
    private ImageView dishImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dish, container, false);
        Bundle bundle = getArguments();
        initElements(v, bundle);
        return v;
    }

    private void initElements(View v, Bundle bundle){
        name = (TextView)v.findViewById(R.id.nameText);
        price = (TextView)v.findViewById(R.id.priceText);
        weight = (TextView)v.findViewById(R.id.weightText);
        description = (TextView)v.findViewById(R.id.descriptionText);
        dishImage = (ImageView) v.findViewById(R.id.dishImage);
        setData(bundle);
    }

    private void setData(Bundle bundle){
        name.setText(bundle.getString(DataBase.COLUMN_NAME));
        price.setText(bundle.getString(DataBase.COLUMN_PRICE));
        description.setText(bundle.getString(DataBase.COLUMN_DESCRIPTION));
        weight.setText(bundle.getString(DataBase.COLUMN_WEIGHT));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.downloading)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity().getApplicationContext()));
        imageLoader.displayImage(bundle.getString(DataBase.COLUMN_PICTURE_URL),dishImage,options);
    }


}

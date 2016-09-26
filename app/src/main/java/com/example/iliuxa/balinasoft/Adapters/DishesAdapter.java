package com.example.iliuxa.balinasoft.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.iliuxa.balinasoft.Data.DataBase;
import com.example.iliuxa.balinasoft.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Iliuxa on 24.09.16.
 */

public class DishesAdapter extends SimpleCursorAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;


    public DishesAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        ViewHolder holder=null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.dishes_list,parent,false);
            holder = new ViewHolder();
            holder.dishImage = (ImageView)convertView.findViewById(R.id.dishImage);
            holder.name = (TextView)convertView.findViewById(R.id.nameText);
            holder.price = (TextView)convertView.findViewById(R.id.priceText);
            holder.weight = (TextView)convertView.findViewById(R.id.weightText);
            convertView.setTag(holder);
        }
        else  holder = (ViewHolder) convertView.getTag();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.downloading)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoader.displayImage(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_PICTURE_URL)),holder.dishImage,options);


        holder.name.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME)));
        holder.price.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_PRICE)));
        holder.weight.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_WEIGHT)));

        return  convertView;
    }

    private class ViewHolder {
        public ImageView dishImage;
        public TextView name;
        public TextView price;
        public TextView weight;
    }
}

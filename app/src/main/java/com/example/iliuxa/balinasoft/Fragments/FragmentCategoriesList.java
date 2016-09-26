package com.example.iliuxa.balinasoft.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.iliuxa.balinasoft.Data.DataBase;
import com.example.iliuxa.balinasoft.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FragmentCategoriesList extends Fragment {

    public interface myOnItemClickListener {
        public void onClick(String Category, int categoryId) throws NoSuchFieldException;
    }

    private GridView gridView;
    private DataBase dataBase;
    private myOnItemClickListener myListener;
    private final String DRAWABLE = "drawable://";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_category_list, container, false);
        getActivity().setTitle(getString(R.string.categoriesTitle));
        dataBase = new DataBase(getActivity().getApplicationContext());
        dataBase.open();

        String[] from = new String[]{DataBase.COLUMN_CATEGORY, DataBase.COLUMN_ID_CATEGORY};
        int[] to = new int[]{R.id.categoryText, R.id.imageCategory};
        MyAdapter adapter = new MyAdapter(getActivity().getApplicationContext(),
                R.layout.categories_list, dataBase.getCategoriesList(),from,to);
        initElements(v,adapter);
        return v;
    }

    private void initElements(View v, CursorAdapter adapter){
        gridView = (GridView)v.findViewById(R.id.categoriesGrid);
        gridView.setNumColumns(2);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.categoryText);
                try {
                    myListener.onClick(textView.getText().toString(),(int)id);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myListener = (myOnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement myOnItemClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataBase.close();
    }

    private class MyAdapter extends SimpleCursorAdapter {
        Context mContext;
        private LayoutInflater layoutInflater;


        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Cursor cursor = (Cursor) getItem(position);
            ViewHolder holder=null;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.categories_list,parent,false);
                holder = new ViewHolder();
                holder.categoryImage = (ImageView)convertView.findViewById(R.id.imageCategory);
                holder.category = (TextView)convertView.findViewById(R.id.categoryText);
                convertView.setTag(holder);
            }
            else  holder = (ViewHolder) convertView.getTag();

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(DRAWABLE + cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_ID_CATEGORY)),holder.categoryImage);
            holder.category.setText(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_CATEGORY)));

            return convertView;
        }

        private class ViewHolder {
            public ImageView categoryImage;
            public TextView category;
        }
    }

}

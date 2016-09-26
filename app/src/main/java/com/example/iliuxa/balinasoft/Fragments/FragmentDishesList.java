package com.example.iliuxa.balinasoft.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.iliuxa.balinasoft.Adapters.DishesAdapter;
import com.example.iliuxa.balinasoft.Data.DataBase;
import com.example.iliuxa.balinasoft.R;

public class FragmentDishesList extends Fragment {



    private GridView gridView;
    private DataBase dataBase;
    private int id;
    private String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dishes_list, container, false);
        dataBase = new DataBase(getActivity().getApplicationContext());
        dataBase.open();
        Bundle bundle = getArguments();
        id = bundle.getInt(DataBase.COLUMN_ID_CATEGORY);
        category = bundle.getString(DataBase.COLUMN_CATEGORY);
        getActivity().setTitle(category);

        String[] from = new String[]{DataBase.COLUMN_NAME, DataBase.COLUMN_PRICE
                , DataBase.COLUMN_WEIGHT, DataBase.COLUMN_PICTURE_URL};
        int[] to = new int[]{R.id.nameText, R.id.priceText, R.id.weightText, R.id.dishImage};
        CursorAdapter adapter = new DishesAdapter(getActivity().getApplicationContext(),
                R.layout.dishes_list, dataBase.getDishesByCategory(id),from,to);
        initElements(v,adapter);
        return v;
    }

    private void initElements(View v, final CursorAdapter adapter){
        gridView = (GridView)v.findViewById(R.id.dishesGrid);
        gridView.setNumColumns(2);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString(DataBase.COLUMN_NAME,cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME)));
                bundle.putString(DataBase.COLUMN_DESCRIPTION,cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_DESCRIPTION)));
                bundle.putString(DataBase.COLUMN_WEIGHT,cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_WEIGHT)));
                bundle.putString(DataBase.COLUMN_PRICE,cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_PRICE)));
                bundle.putString(DataBase.COLUMN_PICTURE_URL,cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_PICTURE_URL)));
                Fragment dish = new DishFragment();
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                dish.setArguments(bundle);
                ft.replace(R.id.container,dish);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataBase.close();
    }

}

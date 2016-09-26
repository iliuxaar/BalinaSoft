package com.example.iliuxa.balinasoft.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBase{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dishes.db";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_DISHES = "dishes";
    public static final String COLUMN_ID_CATEGORY = "_id";
    public static final String COLUMN_ID_DISH = "_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_CATEGORYID = "categoryID";
    public static final String COLUMN_PICTURE_URL = "pictureUrl";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String DRAWABLE = "drawable";
    private final int SIZE_RAW = 2;

    private final Context context;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DataBase(Context context) {
        this.context = context;
    }

    public void open(){
        dataBaseHelper = new DataBaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close(){
        if(dataBaseHelper!=null)
            dataBaseHelper.close();
    }

    public void addToBD(ContentValues contentValues, String table){
        if(contentValues != null)
            sqLiteDatabase.insert(table, null, contentValues);

    }

    public Cursor getAllData(String table){
        return sqLiteDatabase.query(table,null,null,null,null,null,null);
    }

    public boolean isNoFieldDishInDataBase(ContentValues contentValues){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + TABLE_DISHES + " where " + COLUMN_NAME +
                "=? and " + COLUMN_PRICE + "=?",new String[]{contentValues.getAsString(COLUMN_NAME), String.valueOf(contentValues.getAsFloat(COLUMN_PRICE))});
        cursor.moveToFirst();
        if(cursor.getCount() == 1) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public boolean isNoFieldCategoryInDataBase(ContentValues contentValues){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + TABLE_CATEGORIES + " where " + COLUMN_CATEGORY +
                "=? ",new String[]{contentValues.getAsString(COLUMN_CATEGORY)});
        cursor.moveToFirst();
        if(cursor.getCount() == 1) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public Cursor getDishesByCategory(int categoryID){
        Cursor cursor = sqLiteDatabase.query(TABLE_DISHES, null, COLUMN_CATEGORYID + " = ? ", new String[]{String.valueOf(categoryID)},null,null,COLUMN_PRICE);
        cursor.moveToFirst();
        cursor.moveToNext();
        return cursor;
    }

    public Cursor getCategoriesList(){
        return sqLiteDatabase.query(TABLE_CATEGORIES, null,null,null,null,null,null);
    }

    public Cursor getDish(int index){
        return sqLiteDatabase.query(TABLE_DISHES,null, COLUMN_ID_DISH + " =?", new String[]{String.valueOf(index)},null,null,null);
    }

    private class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context, String table, SQLiteDatabase.CursorFactory factory, int version){
            super(context, table, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_CATEGORIES +" (" + COLUMN_ID_CATEGORY
                    + " INTEGER," + COLUMN_CATEGORY + " TEXT);");
            db.execSQL("CREATE TABLE " + TABLE_DISHES +" (" + COLUMN_ID_DISH + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT, " + COLUMN_PRICE + " REAL, " + COLUMN_PICTURE_URL + " TEXT, "
                    + COLUMN_WEIGHT + " TEXT, " + COLUMN_CATEGORYID + " INTEGER, " + COLUMN_DESCRIPTION + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public boolean isDataBaseEmpty(){
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null).getCount() <= 0
                || sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null).getCount() <= 0;
    }
}

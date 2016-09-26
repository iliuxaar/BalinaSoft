package com.example.iliuxa.balinasoft.Singleton;

import android.content.ContentValues;
import android.content.Context;

import com.example.iliuxa.balinasoft.Data.DataBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParserSingleton {
    private final String URL_ADDRESS = "http://ufa.farfor.ru/getyml/?key=ukAXxeJYZN";
    private final String NAME = "name";
    private final String DESCRIPTION = "description";
    private final String PICTURE = "picture";
    private final String PRICE = "price";
    private final String WEIGHT = "Вес";
    private final String CATEGORYID = "categoryId";
    private final String CATEGORY = "category";
    private final String OFFER = "offer";
    private final String PARAM = "param";
    private final int ZERO_INDEX = 0;
    private final String ID = "id_";
    private DataBase dataBase;

    private static ParserSingleton ourInstance = new ParserSingleton();
    public static ParserSingleton getInstance() {
        return ourInstance;
    }
    private ParserSingleton() {
    }



    public void parse(Context context) throws XmlPullParserException, IOException {
        setDataBase(context);
        XmlPullParser xpp = prepareXpp(getHttpRequest(URL_ADDRESS));
        String tagValue = null;
        ContentValues contentValues = null;
        Boolean categoryFlag = false;
        Boolean offerFlag = false;
        Boolean weightFlag = false;
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            switch (xpp.getEventType()) {
                case XmlPullParser.START_TAG:
                    if(xpp.getName().equalsIgnoreCase(CATEGORY)) {
                        categoryFlag = true;
                        contentValues = new ContentValues();
                        contentValues.put(DataBase.COLUMN_ID_CATEGORY ,context.getResources().
                                getIdentifier(ID + xpp.getAttributeValue(ZERO_INDEX),DataBase.DRAWABLE,context.getPackageName()));
                    }
                    if(xpp.getName().equalsIgnoreCase(OFFER)){
                        offerFlag = true;
                        contentValues = new ContentValues();
                    }
                    if(xpp.getName().equalsIgnoreCase(PARAM)){
                        if(xpp.getAttributeValue(ZERO_INDEX).equalsIgnoreCase(WEIGHT))
                            weightFlag =true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    tagValue = xpp.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if(categoryFlag){
                        contentValues.put(DataBase.COLUMN_CATEGORY,tagValue);
                        if(dataBase.isNoFieldCategoryInDataBase(contentValues))
                            dataBase.addToBD(contentValues,DataBase.TABLE_CATEGORIES);
                        categoryFlag = false;
                    }
                    if(offerFlag){
                        saveFiledsInDataBase(xpp,contentValues,tagValue,context);
                        if(weightFlag){
                            contentValues.put(DataBase.COLUMN_WEIGHT, tagValue);
                            weightFlag = false;
                        }
                    }
                    if(xpp.getName().equalsIgnoreCase(OFFER)) {
                        if(dataBase.isNoFieldDishInDataBase(contentValues))
                            dataBase.addToBD(contentValues,DataBase.TABLE_DISHES);
                        offerFlag = false;
                    }
                    break;

                default:break;
            }
            xpp.next();
        }
        dataBase.close();
    }

    private void saveFiledsInDataBase(XmlPullParser xpp, ContentValues contentValues, String tagValue, Context context){
        if(xpp.getName().equalsIgnoreCase(NAME))
            contentValues.put(DataBase.COLUMN_NAME,tagValue);
        if(xpp.getName().equalsIgnoreCase(DESCRIPTION))
            contentValues.put(DataBase.COLUMN_DESCRIPTION,tagValue);
        if(xpp.getName().equalsIgnoreCase(PRICE))
            contentValues.put(DataBase.COLUMN_PRICE, Float.parseFloat(tagValue));
        if(xpp.getName().equalsIgnoreCase(PICTURE))
            contentValues.put(DataBase.COLUMN_PICTURE_URL, tagValue);
        if(xpp.getName().equalsIgnoreCase(CATEGORYID))
            contentValues.put(DataBase.COLUMN_CATEGORYID, context.getResources().
                    getIdentifier(ID + tagValue,DataBase.DRAWABLE,context.getPackageName()));

    }

    public void setDataBase(Context context){
        dataBase = new DataBase(context);
        dataBase.open();
    }

    private String getHttpRequest(String path) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private XmlPullParser prepareXpp(String rss) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(rss));
        return xpp;
    }


}

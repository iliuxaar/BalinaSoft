package com.example.iliuxa.balinasoft.Controllers;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iliuxa.balinasoft.Data.DataBase;
import com.example.iliuxa.balinasoft.Fragments.FragmentCategoriesList;
import com.example.iliuxa.balinasoft.Fragments.FragmentContacts;
import com.example.iliuxa.balinasoft.Fragments.FragmentDishesList;
import com.example.iliuxa.balinasoft.R;
import com.example.iliuxa.balinasoft.Singleton.ParserSingleton;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentCategoriesList.myOnItemClickListener {

    private Button download;
    private TextView eptyDataBaseText;
    private ProgressBar progressBar;
    private DataBase dataBase;
    private FragmentTransaction ft;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        initImageLoader(getApplicationContext());
        setContentView(R.layout.activity_list_of_dishes);
        setTitle(R.string.categoriesTitle);
        initElements();
        initNavigationView();
        isEmptyDataBase();
    }

    private void initImageLoader(Context context){
        File cacheDir = StorageUtils.getCacheDirectory(context);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480 ,800)
                .discCacheExtraOptions(480, 800, null)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(context))
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_of_dishes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                openUpdateDialog();


        }
        return super.onOptionsItemSelected(item);
    }

    private void openUpdateDialog(){
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(R.string.updatingText);
        quitDialog.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearStack(getFragmentManager());
                ft = getFragmentManager().beginTransaction();
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), R.string.networkIsnotAvaliable, Toast.LENGTH_LONG).show();
                    return;
                }
                DownloadDataBase ddb = new DownloadDataBase();
                ddb.execute();
            }
        });
        quitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    private void clearStack(FragmentManager fragmentManager){
        int count = fragmentManager.getBackStackEntryCount();
        while(count > 0){
            fragmentManager.popBackStack();
            count--;
        }
    }

    private void initNavigationView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_catalog);
    }

    private void initElements(){
        download = (Button)findViewById(R.id.dowloadButton);
        eptyDataBaseText = (TextView)findViewById(R.id.emptyBaseText);
        progressBar = (ProgressBar)findViewById(R.id.progressDownload);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), R.string.networkIsnotAvaliable, Toast.LENGTH_LONG).show();
                    return;
                }
                DownloadDataBase ddb = new DownloadDataBase();
                ddb.execute();
            }
        });
    }

    private void isEmptyDataBase(){
        dataBase = new DataBase(getApplicationContext());
        dataBase.open();
        if(dataBase.isDataBaseEmpty()) {
            dataBase.close();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else {
            download.setVisibility(View.GONE);
            eptyDataBaseText.setVisibility(View.GONE);
            Fragment fragmentCatalog = new FragmentCategoriesList();
            addFragment(fragmentCatalog);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if(count == 1)
                openQuitDialog();
            else getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_catalog) {
            Fragment fragmentCatalog = new FragmentCategoriesList();
            addFragment(fragmentCatalog);
        }
        else if (id == R.id.nav_contacts) {
            Fragment fragmentContacts = new FragmentContacts();
            addFragment(fragmentContacts);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addFragment(Fragment fragment){
        ft = getFragmentManager().beginTransaction();
        Fragment temp = fragment;
        ft.replace(R.id.container,temp);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBase.close();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(R.string.quit_text);
        quitDialog.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        quitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public void onClick(String category, int categoryId) throws NoSuchFieldException {
        Fragment dishesList = new FragmentDishesList();
        Bundle bundle = new Bundle();
        bundle.putString(DataBase.COLUMN_CATEGORY,category);
        bundle.putInt(DataBase.COLUMN_ID_CATEGORY, categoryId);
        dishesList.setArguments(bundle);
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, dishesList);
        ft.addToBackStack(null);
        ft.commit();
    }

    public class DownloadDataBase extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ParserSingleton.getInstance().parse(getApplicationContext());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eptyDataBaseText.setText(R.string.updatingBase);
            download.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            setTitle(getString(R.string.updating));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            eptyDataBaseText.setVisibility(View.GONE);
            Fragment fragmentCatalog = new FragmentCategoriesList();
            addFragment(fragmentCatalog);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            setTitle(R.string.categoriesTitle);
        }
    }
}

package com.droidprogramming.automatedearlywarningsystem.ui;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import com.droidprogramming.automatedearlywarningsystem.R;
import com.droidprogramming.automatedearlywarningsystem.fragments.DetailsFragment;
import com.droidprogramming.automatedearlywarningsystem.fragments.ListFragment;
import com.droidprogramming.automatedearlywarningsystem.interfaces.OnRecipeSelectedInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements OnRecipeSelectedInterface {
    public static final String KEY_FOR_URL_PASSING = "KEY_FOR_URL_PASSING";
    private static final String TAG = MainActivity.class.getSimpleName();
    private AdView mAdView;

    private ListFragment mListFragment;
    private android.app.FragmentTransaction fragmentTransaction;




    // handling more than 64 lines on methods to older devices & Android OS versions!
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Showing Toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Implementing Google AdMob
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




        // Fragments handling so we don't end up with duplicated listView on the ui.
        mListFragment = new ListFragment();
        try {
            ListFragment savedFragment = (ListFragment) getFragmentManager().findFragmentById(R.id.placeHolder);

            if (savedInstanceState == null) {
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.placeHolder, mListFragment);
                fragmentTransaction.commit();
            }
        } catch (ClassCastException e) {
            DetailsFragment savedFragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.placeHolder);

            if (savedInstanceState == null) { // so we don't end up with duplicated listView
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.placeHolder, mListFragment);
                fragmentTransaction.commit();
            }
        }

    }



    @Override
    public void onListRecipeSelected(int index, String url) {

        DetailsFragment detailsFragment = new DetailsFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(mListFragment);

        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.KEY_FOR_URL_PASSING, url);
        detailsFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.placeHolder, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

}
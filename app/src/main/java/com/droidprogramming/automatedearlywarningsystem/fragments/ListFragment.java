package com.droidprogramming.automatedearlywarningsystem.fragments;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidprogramming.automatedearlywarningsystem.R;
import com.droidprogramming.automatedearlywarningsystem.adapters.ListAdapter;
import com.droidprogramming.automatedearlywarningsystem.async.ListAsync;
import com.droidprogramming.automatedearlywarningsystem.interfaces.MainActivityAsyncListenerResponse;
import com.droidprogramming.automatedearlywarningsystem.interfaces.OnRecipeSelectedInterface;
import com.droidprogramming.automatedearlywarningsystem.models.Warnings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by mohammadlaif on 10/3/16.
 */

public class ListFragment extends android.app.ListFragment implements MainActivityAsyncListenerResponse {
    private static final String TAG = ListFragment.class.getSimpleName();
    private ListView mListView;
    private ListAdapter mListListAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListAsync mListAsync;
    private TextView mNoWarningTextView;

    // ads
    private int isItTimeToShowAds;
    private InterstitialAd mInterstitialAd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) view.findViewById(android.R.id.list); // or getListView()
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarMainScreen);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mNoWarningTextView = (TextView) view.findViewById(R.id.noWarningTextView);


        if (isNetworkAvailable()) {
            grabData();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    grabData();
                    showAds();
                    mSwipeRefreshLayout.setRefreshing(false);

                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mListView.setAdapter(null);
                }
            }
        });

        isItTimeToShowAds = 0;
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id_for_test));


        return view;
    }


    private void grabData() {
        mNoWarningTextView.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mListAsync = new ListAsync(this.getActivity()); // to fire more than one async, each refresh time different one.
        mListAsync.execute();
        mListAsync.delegate = this;
    }


    private boolean isNetworkAvailable() {
        String messageNoInternet = "يرجى التحقق من الاتصال بالانترنت, ثم اعد المحاولة بالسحب الى الاسفل";
        ConnectivityManager manager = (ConnectivityManager) this.getActivity().getSystemService(this.getActivity().getApplicationContext().CONNECTIVITY_SERVICE); // ? i add: this.getactivity(
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this.getActivity(), messageNoInternet, Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        return isAvailable;
    }

    private void moreApps() {
        String developerLink = "https://goo.gl/V2nqXv";

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(developerLink)));
        } catch (android.content.ActivityNotFoundException activityNotFoundException){
            Log.d("ListFragment", "moreApps: no app to open this link");
        }

    }

    private void showAds() {
//        mInterstitialAd = new InterstitialAd(getActivity());


        // request the ad
        if (!mInterstitialAd.isLoaded()) {
            Log.d(TAG, "isLoaded: " + String.valueOf(mInterstitialAd.isLoaded()));
            AdRequest ar = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(ar);
        }

        // show the ad
        Log.d(TAG, "showAds: isItTimeToShowAds" + String.valueOf(isItTimeToShowAds));
        if (isItTimeToShowAds % 3 == 0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        ++isItTimeToShowAds;
        Log.d(TAG, "showAds: isItTimeToShowAds" + String.valueOf(isItTimeToShowAds));
    }


    @Override
    public void processFinish(Warnings[] warningses) {
        mListListAdapter = new ListAdapter(this.getActivity(), warningses);
        setListAdapter(mListListAdapter);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mListAsync.mWarningses == null) {
        } else {
            String url = mListAsync.mWarningses[position].getLink();
            OnRecipeSelectedInterface listener = (OnRecipeSelectedInterface) getActivity();
            listener.onListRecipeSelected(position, url);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh_main:
                if (isNetworkAvailable()) {
                    grabData();
                }
                break;
            case R.id.more_apps:
                moreApps();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}

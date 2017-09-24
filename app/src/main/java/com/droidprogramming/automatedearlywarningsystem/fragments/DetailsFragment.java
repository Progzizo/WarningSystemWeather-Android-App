package com.droidprogramming.automatedearlywarningsystem.fragments;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.droidprogramming.automatedearlywarningsystem.ui.MainActivity;
import com.droidprogramming.automatedearlywarningsystem.R;
import com.droidprogramming.automatedearlywarningsystem.async.DetailsAsync;

/**
 * Created by mohammadlaif on 10/3/16.
 */

public class DetailsFragment extends android.app.Fragment {
    ProgressBar mProgressBar;
    String url;
    ScrollView mDetails_container_scrollable_view;

    TextView mRegionLocations;
    TextView caseLocations;
    TextView caseTextView;
    TextView caseDescriptionTextView;
    TextView caseBeginningTextView;
    TextView caseEndingTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        mRegionLocations = (TextView) view.findViewById(R.id.RegionandLocations);
        caseLocations = (TextView) view.findViewById(R.id.caseLocations);
        caseTextView = (TextView) view.findViewById(R.id.caseTextView);
        caseDescriptionTextView = (TextView) view.findViewById(R.id.caseDescriptionTextView);
        caseBeginningTextView = (TextView) view.findViewById(R.id.caseBeginningTextView);
        caseEndingTextView = (TextView) view.findViewById(R.id.caseEndingTextView);
        mDetails_container_scrollable_view = (ScrollView) view.findViewById(R.id.details_container_scrollable_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar2);

        url = getArguments().getString(MainActivity.KEY_FOR_URL_PASSING);

        if (isNetworkAvailable()) {
            grabData();
        }

        return view;
    }


    private void grabData() {
        mDetails_container_scrollable_view.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        new DetailsAsync(this.getActivity()).execute(url);
    }


    private boolean isNetworkAvailable() {
        String messageNoInternet = "يرجى التحقق من الاتصال بالانترنت, ثم اعد المحاولة بأعادة الدخول";
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


    private void shareIntent() {
        StringBuilder statusInformation = new StringBuilder();

        String bottomText = "*تمت المشاركة بواسطة تطبيق الانذار المبكر*";
        String appLink = "https://goo.gl/qOJrfk";

        statusInformation.append("*المنطقة:* ");
        statusInformation.append(mRegionLocations.getText());
        statusInformation.append("\n");
        statusInformation.append("*موقع البلاغ:* ");
        statusInformation.append(caseLocations.getText());
        statusInformation.append("\n");
        statusInformation.append("\n");
        statusInformation.append("*الحالة:* ");
        statusInformation.append(caseTextView.getText());
        statusInformation.append("\n");
        statusInformation.append("*وصف الحالة:* ");
        statusInformation.append(caseDescriptionTextView.getText());
        statusInformation.append("\n");
        statusInformation.append("\n");
        statusInformation.append("*بداية الحالة:*");
        statusInformation.append("\n");
        statusInformation.append(caseBeginningTextView.getText());
        statusInformation.append("\n");
        statusInformation.append("*نهاية الحالة:*");
        statusInformation.append("\n");
        statusInformation.append(caseEndingTextView.getText());
        statusInformation.append("\n");
        statusInformation.append("\n");
        statusInformation.append(bottomText);
        statusInformation.append("\n");
        statusInformation.append(appLink);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, statusInformation.toString());
        startActivity(intent.createChooser(intent, "اختر التطبيق المراد المشاركة اليه"));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDetails_container_scrollable_view.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh_details:
                if (isNetworkAvailable()) {
                    grabData();
                }
                break;

            case R.id.share_details:
                shareIntent();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
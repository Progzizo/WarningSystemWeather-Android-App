package com.droidprogramming.automatedearlywarningsystem.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.droidprogramming.automatedearlywarningsystem.R;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by mohammadlaif on 10/3/16.
 */

public class DetailsAsync extends AsyncTask<String, Void, Void> {
    private String mRegionTextView;
    private String mCaseBeginningTextView;
    private String mCaseEndingTextView;
    private String mCaseDescriptionTextView;
    private String mCaseTextView;
    private String mCaseLocations;
    private WeakReference<Activity> mWeakReference;


    public DetailsAsync(Activity activity) {
        mWeakReference = new WeakReference<Activity>(activity);
    }

    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];

        try {
            org.jsoup.nodes.Document document = Jsoup.connect(url).get();
//            Log.d("NEWFIX", document.tagName());
//            String htmlClassTag = "gray_bg";
            String htmlClassTag = "alert-table";
            Elements elementsOfClassTag = document.getElementsByClass(htmlClassTag);


//            // for debugging when the link change
//            for (Element ele: elementsOfClassTag) {
////                Log.d("NEWFIX", ele.textNodes());
//
//                for (Node node: ele.childNodes()){
//                    Log.d("NEWFIX", node.toString());
//                }
//            }
//
//            Log.d("NEWFIX2", elementsOfClassTag.get(0).getAllElements().toString());
//            Log.d("NEWFIX3", elementsOfClassTag.get(0).getAllElements().get(4).toString());
//
//            int size = elementsOfClassTag.get(0).getAllElements().size();
//            for (int i = 0; i < size; i++) {
//                Log.d("NEWFIX3", i + "---->" +  elementsOfClassTag.get(0).getAllElements().get(i).text());
//
//            }


            mRegionTextView = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(10).text());
            mCaseBeginningTextView =  String.valueOf(elementsOfClassTag.get(0).getAllElements().get(35).text());
            mCaseEndingTextView = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(40).text());
            mCaseDescriptionTextView = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(45).text());
            mCaseTextView = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(30).text());
            String caseLocations1 = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(15).text());
            String caseLocations2 = String.valueOf(elementsOfClassTag.get(0).getAllElements().get(20).text());
            mCaseLocations = caseLocations1 + "\n" + caseLocations2;

//            mRegionTextView = String.valueOf(elementsOfClassTag.get(1).text());
//            mCaseBeginningTextView = String.valueOf(elementsOfClassTag.get(9).text());
//            mCaseEndingTextView = String.valueOf(elementsOfClassTag.get(11).text());
//            mCaseDescriptionTextView = String.valueOf(elementsOfClassTag.get(13).text());
//            mCaseTextView = String.valueOf(elementsOfClassTag.get(7).text());
//            mCaseLocations = String.valueOf(elementsOfClassTag.get(3).text());

        } catch (IOException | RuntimeException e) { // RuntimeException when data not find cuz they change the link and the html code
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Activity activity = mWeakReference.get();

        ScrollView mDetails_layout = (ScrollView) activity.findViewById(R.id.details_container_scrollable_view);
        TextView regionTextView = (TextView) activity.findViewById(R.id.RegionandLocations);
        TextView caseBeginningTextView = (TextView) activity.findViewById(R.id.caseBeginningTextView);
        TextView caseEndingTextView = (TextView) activity.findViewById(R.id.caseEndingTextView);
        TextView caseDescriptionTextView = (TextView) activity.findViewById(R.id.caseDescriptionTextView);
        TextView caseTextView = (TextView) activity.findViewById(R.id.caseTextView);
        TextView caseLocations = (TextView) activity.findViewById(R.id.caseLocations);
        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar2);
        LinearLayout mDetails_container_data = (LinearLayout) activity.findViewById(R.id.details_container_data);

        // When user clicks back button, views destroyed and get null.
        // so I check if a view is null then I canceled the async task.
        // else continue working

        // fixing this issue
        // https://console.firebase.google.com/project/admob-app-id-4592869261/monitoring/app/android:com.droidprogramming.automatedearlywarningsystem/cluster/c5f947ab

        if (regionTextView == null) {
            cancel(true);
        } else {
            if (mRegionTextView == null) {
                String messageNoInternet = "يرجى التحقق من الاتصال بالانترنت, ثم اعد المحاولة بأعادة الدخول, يرجى التحقق من وجود اشارة انترنت قوية";
                Toast.makeText(activity, messageNoInternet, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);

            } else {
                regionTextView.setText(mRegionTextView);
                caseBeginningTextView.setText(mCaseBeginningTextView);
                caseEndingTextView.setText(mCaseEndingTextView);
                caseDescriptionTextView.setText(mCaseDescriptionTextView);
                caseTextView.setText(mCaseTextView);
                caseLocations.setText(mCaseLocations);

                progressBar.setVisibility(View.INVISIBLE);
                mDetails_layout.setVisibility(View.VISIBLE);
                mDetails_container_data.setVisibility(View.VISIBLE);
            }
        }
    }
}

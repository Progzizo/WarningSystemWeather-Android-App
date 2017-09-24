package com.droidprogramming.automatedearlywarningsystem.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.droidprogramming.automatedearlywarningsystem.interfaces.MainActivityAsyncListenerResponse;
import com.droidprogramming.automatedearlywarningsystem.R;
import com.droidprogramming.automatedearlywarningsystem.models.Warnings;
import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Mzdhr on 9/12/16.
 */
public class ListAsync extends AsyncTask<Void, Void, Void> {
    private static final String TAG = ListAsync.class.getSimpleName();
    public MainActivityAsyncListenerResponse delegate = null;
    public Warnings[] mWarningses;
    private String mLastUpdateLabel;
    private WeakReference<Activity> mWeakReference;


    public ListAsync(Activity activity) {
        mWeakReference = new WeakReference<Activity>(activity);
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {
            String webPageURL = "http://www.pme.gov.sa/Ar/alert/Pages/default.aspx";
            org.jsoup.nodes.Document webPage = Jsoup.connect(webPageURL).get();
            Elements elements = webPage.getElementsByTag("script"); // where data are.

            // Debugging where data is, because each time they change it's element index.
            for (int i = 0; i < elements.size(); i++) {
                Log.d(TAG, "doInBackground: elements " + "Index is ---> " + i + elements.get(i));
            }


            String scriptHTMLAsText = elements.get(76).toString(); // where data regions are // before was 6 // fixed to 74 second time
            Log.d(TAG, "doInBackground: scriptHTMLAsText: " + scriptHTMLAsText);

            // putting data into string, so we treat (in parsing) it as html code.
            String extractedHTML = "";
            extractedHTML = scriptHTMLAsText.substring(scriptHTMLAsText.indexOf('['), scriptHTMLAsText.lastIndexOf(']'));
            Log.d(TAG, "doInBackground: extractedHTML: " + extractedHTML);
            org.jsoup.nodes.Document html = Jsoup.parse(extractedHTML);
            Log.d(TAG, "doInBackground: html: " + html);

            // creating elements to grab <p> and <a> tags in our extracted html.
            // <p> -> <p><b>منطقة عسير</b></p
            // <a> -> <a href="v-25993" title="المزيد من التفصيل"><b>وصف الحالة</b></a>
            Elements pTags = html.getElementsByTag("p");
            Elements aTags = html.getElementsByTag("a");
            Log.d(TAG, "doInBackground: pTags: " + pTags);
            Log.d(TAG, "doInBackground: aTags: " + aTags);

            mLastUpdateLabel = webPage.getElementsByClass("last_time").text();

            // creating Queue array to handel our a tags data (first in first out)
            Queue<String> allData = new ArrayDeque<>();
            Queue<String> linkData = new ArrayDeque<>();


            for (Element a : aTags) {
                linkData.add(a.attr("href"));
            }

            for (Element p : pTags) {
                if (p.text().contentEquals("وصف الحالة")) {
                } else {
                    allData.add(p.text());
                }
            }

            // Sizing warning objects lists
            mWarningses = new Warnings[linkData.size()]; // to get the right number
            Log.d(TAG, "doInBackground: mWarningses Size: " + mWarningses.length);

            // creating objects and Adding data to warning objects lists
            Integer limit = linkData.size(); // linkData size is changing, so made a variable for it.
            for (int i = 0; i < limit; i++) {
                mWarningses[i] = new Warnings(allData.poll(), allData.poll(), linkData.poll());
            }


            // dump data for testing:

//            mWarningses = new Warnings[2];
//             for (int i = 0; i < 20; i++) {
//                 mWarningses[i] = new Warnings("خميس حرب - الفالق - باتجاه سبت الجارة والاجزاء المجاورة"
//                         , "سحب رعدية"
//                         , "/v-25993");
//             }


        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Activity activity = mWeakReference.get();
        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBarMainScreen);
        TextView lastUpdate = (TextView) activity.findViewById(R.id.lastUpdateLabel);
        TextView noWarningTextView = (TextView) activity.findViewById(R.id.noWarningTextView);

        // to fix null object when back button is pressed while fetching data.
        // # 3 when user enter the app, then when almost refrshingand he quit the app it crashs, by the back button
        // java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.View.setVisibility(int)' on a null object reference.

        if (progressBar == null) {
            cancel(true);
        } else {

            if (mWarningses == null) {
                String message = "حدث خطئ! الرجاء معاودة المحاولة بالسحب الى الاسفل, مع وجود اشارة انترنت قوية";
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                lastUpdate.setText("");

                noWarningTextView.setVisibility(View.INVISIBLE);
            } else {
                delegate.processFinish(mWarningses);
                lastUpdate.setText(mLastUpdateLabel);
                progressBar.setVisibility(View.INVISIBLE);
                if (mWarningses.length == 0) {
                    Toast.makeText(activity, "لايوجد انذارات حالياً", Toast.LENGTH_LONG).show();

                    noWarningTextView.setVisibility(View.VISIBLE);
                    noWarningTextView.setText("لايوجد انذارات حالياً");
                }
            }


        }
    }

}

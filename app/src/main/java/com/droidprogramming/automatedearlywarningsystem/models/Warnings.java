package com.droidprogramming.automatedearlywarningsystem.models;

import android.util.Log;

/**
 * Created by Mzdhr on 9/11/16.
 */
public class Warnings {
    private String mRegion;
    private String mStatus;
    private String mLink;


    public Warnings(String region, String status, String link) {
        mRegion = region;
        mStatus = status;
        mLink = link;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getLink() {
//        String fullLink = "http://ew.sa/" + mLink; // old
//        String fullLink = "http://ew.sa/" + mLink;


//        String fullLink = "http://ew.sa/" + mLink;
//        Log.d("NEWFIX", mLink);
        // v-25993
        // http://ew.sa/v-25993
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

}

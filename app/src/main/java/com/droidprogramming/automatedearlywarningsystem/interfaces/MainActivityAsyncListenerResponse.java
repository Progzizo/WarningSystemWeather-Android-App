package com.droidprogramming.automatedearlywarningsystem.interfaces;

import com.droidprogramming.automatedearlywarningsystem.models.Warnings;

/**
 * Created by Mzdhr on 9/16/16.
 */
public interface MainActivityAsyncListenerResponse {
    // http://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
    void processFinish(Warnings[] warningses);
}

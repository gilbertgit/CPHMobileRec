package com.cphandheld.cphmobilerec;

import android.app.Application;

import com.splunk.mint.Mint;

/**
 * Created by titan on 10/10/16.
 */
public class CPHMobileRec extends Application {
        @Override public void onCreate() {
            super.onCreate();
            // Set up Splunk Ming
            Mint.setApplicationEnvironment(Mint.appEnvironmentTesting);
            Mint.initAndStartSession(CPHMobileRec.this, getString(R.string.mint_splunk_api_key));
        }
}

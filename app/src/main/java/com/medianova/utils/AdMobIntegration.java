package com.medianova.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.medianova.doctorfinder.R;

/**
 * Created by Android on 2/12/2018.
 */

public class AdMobIntegration {

    //Load And Show Ads
    public static boolean shouldDisplayAds(Context context) {
        String displayAds = context.getString(R.string.diaplayads);
        switch (displayAds) {
            case "yes":
                return true;
            default:
                return false;
        }

    }

    public static void loadAdmobBanner(AdView adView,Context context) {
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static InterstitialAd loadAdmobInterstial(Context context) {
        InterstitialAd mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.adMobInterstial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        return mInterstitialAd;
    }

    public static void showAdmobInterstial(InterstitialAd mInterstitialAd) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}

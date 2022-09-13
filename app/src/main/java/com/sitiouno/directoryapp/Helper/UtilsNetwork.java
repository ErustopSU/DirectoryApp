package com.sitiouno.directoryapp.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

 public class UtilsNetwork {
    public static boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(
                 Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                return  networkInfo.isConnected();
            }
        }
        return false;
    }
}

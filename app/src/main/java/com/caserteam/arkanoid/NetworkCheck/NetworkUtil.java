package com.caserteam.arkanoid.NetworkCheck;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class NetworkUtil {
    public static final String TAG = "NetworkStateInfo";
    private OnConnectionStatusChange onConnectionStatusChange;
    public NetworkUtil(){}

    public void checkNetworkInfo(Context context, OnConnectionStatusChange listener) {

        onConnectionStatusChange = listener;
        if(onConnectionStatusChange != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities == null){
                    onConnectionStatusChange.onChange(false);
                }
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Log.e(NetworkUtil.TAG,"onAvaible");
                        IsInternetActiveTask task = new IsInternetActiveTask(onConnectionStatusChange);
                        task.execute();

                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        Log.e(NetworkUtil.TAG,"onLost");
                        onConnectionStatusChange.onChange(false);
                    }
                });

            } else {

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean status = getStatusConnection(networkInfo);
                onConnectionStatusChange.onChange(status);

            }
        }

    }

    public boolean checkDialogPresence(@NotNull Context context, AppCompatActivity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean status = getStatusConnection(networkInfo);

        if (status) {
            Toast.makeText(context,"Connected",Toast.LENGTH_SHORT);
        } else {
            OfflineFragment offlineFragment = new OfflineFragment();
            offlineFragment.show(activity.getSupportFragmentManager(),"Dialog");
        }
        return status;
    }

    public boolean getStatusConnection(NetworkInfo networkInfo){
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    static class IsInternetActiveTask extends AsyncTask<Void, Void, String>
    {
        InputStream is = null;
        String json = "Fail";
        OnConnectionStatusChange mlistener;

        public IsInternetActiveTask(OnConnectionStatusChange listener){
            mlistener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL strUrl = new URL("https://google.com");
                //Here I have taken one android small icon from server, you can put your own icon on server and access your URL, otherwise icon may removed from another server.

                HttpURLConnection connection = (HttpURLConnection) strUrl.openConnection();
                connection.setDoOutput(true);
                Log.e(NetworkUtil.TAG,String.valueOf(connection.getResponseCode()));
                json = "Success";

            } catch (Exception e) {
                e.printStackTrace();
                json = "Fail";
            }
            return json;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
            {
                if(result.equals("Fail")) { mlistener.onChange(false); } else { mlistener.onChange(true); }
            }
            else { mlistener.onChange(false); }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }

    public void setOnConnectionStatusChange(OnConnectionStatusChange onConnectionStatusChange){
        this.onConnectionStatusChange = onConnectionStatusChange;
    }
    public interface OnConnectionStatusChange{
        void onChange(boolean type);
    }

}


package com.caserteam.arkanoid.NetworkCheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.caserteam.arkanoid.MenuActivity;
import com.caserteam.arkanoid.R;


public class OfflineFragment extends DialogFragment implements NetworkUtil.OnConnectionStatusChange{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_offline, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        NetworkUtil.checkNetworkInfo(context,this);
    }

    @Override
    public void onChange(boolean type) {
        if (type) {
            this.dismissAllowingStateLoss();
        }
    }
}
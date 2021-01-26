package com.caserteam.arkanoid.ui.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.caserteam.arkanoid.R;

import java.util.ArrayList;


public class AdapterListViewScore extends ArrayAdapter<LeaderBoardModel> {

    private Context mContext;
    private int mResource;

    public AdapterListViewScore(Context context, int resource, ArrayList<LeaderBoardModel> leaderBoardModelArrayList) {
        super(context,resource,leaderBoardModelArrayList);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView =layoutInflater.inflate(mResource,parent,false);

        TextView textViewNickname = convertView.findViewById(R.id.nicknamePlayer);
        TextView textViewScore = convertView.findViewById(R.id.scorePlayer);

        String nickname = getItem(position).getNickname();
        String score = String.valueOf(getItem(position).getScore());

        textViewNickname.setText(nickname);
        textViewScore.setText(score);

        return convertView;
    }


}
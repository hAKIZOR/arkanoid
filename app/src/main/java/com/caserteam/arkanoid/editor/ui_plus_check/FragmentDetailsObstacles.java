package com.caserteam.arkanoid.editor.ui_plus_check;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.caserteam.arkanoid.editor.Paddle;
import com.caserteam.arkanoid.R;

public class FragmentDetailsObstacles extends Fragment {
    private FragmentDetailsObstaclesListener mListener;
    private ImageButton buttonObstacleSkin1Show;
    private ImageButton buttonObstacleSkin2Show;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.obstacles_detail_fragment,container,false);

        buttonObstacleSkin2Show = v.findViewById(R.id.imageButtonObstacle2);

        callClickListenerOf(buttonObstacleSkin1Show, Paddle.PADDLE_SKIN1);


        return v;
    }
    private void callClickListenerOf (ImageButton button, int obstacleSkin){
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                mListener.onObstacleClicked(obstacleSkin);
            }
        });
    }

    public  interface FragmentDetailsObstaclesListener{
        void onObstacleClicked(int obstacleSkin);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (FragmentDetailsObstaclesListener)context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }

    }
}

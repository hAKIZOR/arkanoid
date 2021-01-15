package com.caserteam.arkanoid.editor.ui_plus_check;


import androidx.fragment.app.Fragment;
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
import com.caserteam.arkanoid.editor.BrickEditor;
import com.caserteam.arkanoid.R;

public class FragmentDetailBricks extends Fragment {
    private FragmentDetailBricksListener mListener;
    private ImageButton buttonBlue;
    private ImageButton buttonLime;
    private ImageButton buttonGreen;
    private ImageButton buttonOrange;
    private ImageButton buttonAqua;
    private ImageButton buttonRed;
    private ImageButton buttonPurple;
    private ImageButton buttonYellow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bricks_detail_fragment,container,false);

        buttonBlue = v.findViewById(R.id.imageButtonBlue);
        buttonLime = v.findViewById(R.id.imageButtonLime);
        buttonGreen = v.findViewById(R.id.imageButtonGreen);
        buttonOrange= v.findViewById(R.id.imageButtonOrange);
        buttonAqua = v.findViewById(R.id.imageButtonAqua);
        buttonRed = v.findViewById(R.id.imageButtonRed);
        buttonPurple = v.findViewById(R.id.imageButtonPurple);
        buttonYellow = v.findViewById(R.id.imageButtonYellow);

        callClickListenerOf(buttonBlue, BrickEditor.BRICK_BLUE);
        callClickListenerOf(buttonLime, BrickEditor.BRICK_LIME);
        callClickListenerOf(buttonGreen, BrickEditor.BRICK_GREEN);
        callClickListenerOf(buttonOrange, BrickEditor.BRICK_ORANGE);
        callClickListenerOf(buttonAqua, BrickEditor.BRICK_AQUA);
        callClickListenerOf(buttonRed, BrickEditor.BRICK_RED);
        callClickListenerOf(buttonPurple, BrickEditor.BRICK_PURPLE);
        callClickListenerOf(buttonYellow, BrickEditor.BRICK_YELLOW);

        return v;
    }
    private void callClickListenerOf (ImageButton button, int ImageColor){
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                mListener.onBrickClicked(ImageColor);
            }
        });
    }

    public  interface FragmentDetailBricksListener{
        void onBrickClicked(int color);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (FragmentDetailBricksListener)context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }

    }
}

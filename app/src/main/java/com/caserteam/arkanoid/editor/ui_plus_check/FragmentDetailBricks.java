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
import static com.caserteam.arkanoid.AppContractClass.*;

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
    private ImageButton buttonBlue2;
    private ImageButton buttonLime2;
    private ImageButton buttonGreen2;
    private ImageButton buttonOrange2;
    private ImageButton buttonAqua2;
    private ImageButton buttonRed2;
    private ImageButton buttonPurple2;
    private ImageButton buttonYellow2;

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
        buttonBlue2 = v.findViewById(R.id.imageButtonBlue2);
        buttonLime2 = v.findViewById(R.id.imageButtonLime2);
        buttonGreen2 = v.findViewById(R.id.imageButtonGreen2);
        buttonOrange2 = v.findViewById(R.id.imageButtonOrange2);
        buttonAqua2 = v.findViewById(R.id.imageButtonAqua2);
        buttonRed2 = v.findViewById(R.id.imageButtonRed2);
        buttonPurple2 = v.findViewById(R.id.imageButtonPurple2);
        buttonYellow2 = v.findViewById(R.id.imageButtonYellow2);

        callClickListenerOf(buttonBlue, BRICK_BLUE);
        callClickListenerOf(buttonLime, BRICK_LIME);
        callClickListenerOf(buttonGreen, BRICK_GREEN);
        callClickListenerOf(buttonOrange, BRICK_ORANGE);
        callClickListenerOf(buttonAqua, BRICK_AQUA);
        callClickListenerOf(buttonRed, BRICK_RED);
        callClickListenerOf(buttonPurple, BRICK_PURPLE);
        callClickListenerOf(buttonYellow, BRICK_YELLOW);
        callClickListenerOf(buttonBlue2, BRICK_BLUE2);
        callClickListenerOf(buttonLime2, BRICK_LIME2);
        callClickListenerOf(buttonGreen2,BRICK_GREEN2);
        callClickListenerOf(buttonOrange2, BRICK_ORANGE2);
        callClickListenerOf(buttonAqua2, BRICK_AQUA2);
        callClickListenerOf(buttonRed2, BRICK_RED2);
        callClickListenerOf(buttonPurple2, BRICK_PURPLE2);
        callClickListenerOf(buttonYellow2, BRICK_YELLOW2);

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

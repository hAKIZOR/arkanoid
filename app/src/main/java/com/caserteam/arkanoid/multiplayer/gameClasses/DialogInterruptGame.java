package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caserteam.arkanoid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

public class DialogInterruptGame extends DialogFragment {

    Activity activity;
    Button buttonConfirm;

    public DialogInterruptGame(Activity activity) {
        this.activity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_interrupt_dialog,container,false);
        getDialog().setCanceledOnTouchOutside(false);

        buttonConfirm = v.findViewById(R.id.imageButtonExitGame);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = NavUtils.getParentActivityIntent (activity);
                activity.startActivity(intent);
                activity.finish();
            }
        });


        return v;
    }

    private void closeDialog() {
        this.dismiss();
    }

}

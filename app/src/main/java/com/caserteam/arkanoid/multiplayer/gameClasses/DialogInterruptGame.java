package com.caserteam.arkanoid.multiplayer.gameClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.caserteam.arkanoid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;

public class DialogInterruptGame extends DialogFragment {

    Activity activity;
    Button buttonConfirm;
    TextView textViewMessage;
    String message;

    public DialogInterruptGame(Activity activity,String message) {
        this.activity = activity;
        this.message = message;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_interrupt_dialog,container,false);
        getDialog().setCanceledOnTouchOutside(false);

        textViewMessage = v.findViewById(R.id.textViewMessage);
        textViewMessage.setText(message);

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

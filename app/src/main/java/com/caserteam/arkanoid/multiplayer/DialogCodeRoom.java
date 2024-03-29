package com.caserteam.arkanoid.multiplayer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.caserteam.arkanoid.R;

public class DialogCodeRoom extends DialogFragment {

    DialogCodeRoomListener dialogCodeRoomListener;
    private ImageButton imageButtonClose;
    private Button buttonCreateRoom;
    private Button buttonJoinRoom;
    private EditText editTextRoomCode;
    private String code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.room_code_dialog,container,false);
        imageButtonClose = v.findViewById(R.id.imageButtonClose);
        editTextRoomCode = v.findViewById(R.id.editTextNicknameInsert);
        buttonCreateRoom = v.findViewById(R.id.buttonCreateRoom);
        buttonJoinRoom = v.findViewById(R.id.buttonJoinRoom);

        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //accedo alla stanza con quel codice e faccio partire il loading Dialog
                code = editTextRoomCode.getText().toString();
                dialogCodeRoomListener.onClickJoinRoom(code);
            }
        });

        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = editTextRoomCode.getText().toString();
                //creo la stanza e faccio partire il loading Dialog

                dialogCodeRoomListener.onClickCreateRoom(code);
            }
        });

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });


        return v;
    }
    public void dismissDialog(){
        this.dismiss();
    }

    public interface DialogCodeRoomListener{
        void onClickCreateRoom(String code);
        void onClickJoinRoom(String code);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            dialogCodeRoomListener = (DialogCodeRoomListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }

    }

    private void closeDialog() {
        this.dismiss();
    }
}

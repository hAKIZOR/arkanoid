package com.caserteam.arkanoid.multiplayer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailBricks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.editor.FirebaseUtility;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.editor_module.Editor;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.caserteam.arkanoid.editor.ui_upload_check.UploadLevelActivity;

public class DialogCodeRoom extends DialogFragment {
    private static final String PATH_COLLECTION = "utenti/Davide/livelli";
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
        editTextRoomCode = v.findViewById(R.id.editTextCodeRoom);
        buttonCreateRoom = v.findViewById(R.id.buttonCreateRoom);
        buttonJoinRoom = v.findViewById(R.id.buttonJoinRoom);

        code = editTextRoomCode.getText().toString();

        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //accedo alla stanza con quel codice e faccio partire il loading Dialog
                dialogCodeRoomListener.onClickJoinRoomListener(code);
            }
        });

        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creo la stanza e faccio partire il loading Dialog
                dialogCodeRoomListener.onClickCreateRoomListener(code);
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

    public interface DialogCodeRoomListener{
        void onClickCreateRoomListener(String code);
        void onClickJoinRoomListener(String code);
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


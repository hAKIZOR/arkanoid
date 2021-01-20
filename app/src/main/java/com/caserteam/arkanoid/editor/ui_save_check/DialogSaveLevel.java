package com.caserteam.arkanoid.editor.ui_save_check;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.caserteam.arkanoid.editor.editor_module.Editor;
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
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.google.firebase.firestore.SetOptions;

public class DialogSaveLevel extends DialogFragment {
    private static final String USERS_COLLECTION = "utenti";
    private static final String LEVELS_COLLECTION = "livelli";
    private static final String SHARED_LEVEL = "livelliCondivisi";
    private ImageButton imageButtonClose;
    private Button buttonSave;
    private EditText editTextLevelName;
    private String nameLevel;
    private String structure;
    private String mainPathCollection;
    private String nickname;
    private String email;

    public DialogSaveLevel(String structure, String nameLevel, String nickname, String email) {
        this.structure = structure;
        this.nameLevel = nameLevel;
        this.nickname = nickname;
        this.email = email;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.level_save_fragment_dialog,container,false);


       mainPathCollection = USERS_COLLECTION+ "/" + email + "/" + LEVELS_COLLECTION;


       imageButtonClose = v.findViewById(R.id.imageButtonClose);
       buttonSave = v.findViewById(R.id.buttonCreateRoom);
       editTextLevelName = v.findViewById(R.id.editTextCodeRoom);

        if(nameLevel != null){
            editTextLevelName.setText(nameLevel);
        }

       imageButtonClose.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               closeDialog();
           }
       });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLevelToRemoteDatabase();
            }
        });
       return v;
    }

    private void addLevelToRemoteDatabase() {


        Map<String,String> data = new HashMap<>();
        data.put("struttura",structure);


        nameLevel = editTextLevelName.getText().toString();
        data.put("nomeLivello",nameLevel);

        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog(getResources().getString(R.string.save_level));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(mainPathCollection)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        data.put("nickname",nickname);
        db.collection(SHARED_LEVEL)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        loadingDialog.dismissDialog();
                        closeDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void closeDialog() {
        this.dismiss();
    }
}

package com.caserteam.arkanoid.editor.ui_save_check;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class DialogSaveLevel extends DialogFragment {
    private static final String PATH_COLLECTION = "utenti/Davide/livelli";
    private ImageButton imageButtonClose;
    private Button buttonSave;
    private EditText editTextLevelName;
    private String nameLevel;
    private String structure;
    private String pathCollection;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.level_save_fragment_dialog,container,false);

       structure = getArguments().getString(EditorActivity.STATE_STRUCTURE);
       nameLevel = getArguments().getString(EditorActivity.STATE_NAME_LEVEL);
       pathCollection = "utenti/"+getArguments().getString(EditorActivity.STATE_CURRENT_USER)+"/livelli";

       imageButtonClose = v.findViewById(R.id.imageButtonClose);
       buttonSave = v.findViewById(R.id.buttonSave);
       editTextLevelName = v.findViewById(R.id.editTextLevelName);
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
        loadingDialog.startDialog(LoadingDialog.MESSAGE_SAVE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(pathCollection)
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

package com.caserteam.arkanoid.editor.ui_save_check;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.caserteam.arkanoid.editor.PromptUtils;
import com.caserteam.arkanoid.editor.editor_module.Editor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.R;
import com.caserteam.arkanoid.editor.ui_upload_check.LoadingDialog;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class DialogSaveLevel extends DialogFragment {
    private static final String USERS_COLLECTION = "utenti";
    private static final String LEVELS_COLLECTION = "livelli";
    private static final String SHARED_LEVEL = "livelliCondivisi";
    public static final String FIELD_NAME_LEVEL = "nomeLivello";
    public static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_STRUCTURE = "struttura";
    private ImageButton imageButtonClose;
    private Button buttonSave;
    private EditText editTextLevelName;
    private String nameLevel;
    private String structure;
    private String mainPathCollectionLevels;
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


       mainPathCollectionLevels = USERS_COLLECTION+ "/" + email + "/" + LEVELS_COLLECTION;


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


        Map<String,Object> data = new HashMap<>();
        data.put(FIELD_STRUCTURE,structure);


        nameLevel = editTextLevelName.getText().toString();
        data.put(FIELD_NAME_LEVEL,nameLevel);

        LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startDialog(getResources().getString(R.string.save_level));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //avvio salvataggio nella collection livelli dell'utente loggato
        db.collection(mainPathCollectionLevels)
                .whereEqualTo(FIELD_NAME_LEVEL,nameLevel)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String id = null;
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }
                            if(id != null) {
                                db.collection(mainPathCollectionLevels).document(id).delete();
                            }
                            db.collection(mainPathCollectionLevels)
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getContext(),"salvataggio livello  riuscito",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),"salvataggio livello non riuscito",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        //avvio salvataggio nella collection livelliCondivisi
        db.collection(SHARED_LEVEL)
                .whereEqualTo(FIELD_NICKNAME,nickname)
                .whereEqualTo(FIELD_NAME_LEVEL,nameLevel)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String id = null;
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }
                            if(id != null) {
                                db.collection(SHARED_LEVEL).document(id).delete();
                            }
                            data.put(FIELD_NICKNAME,nickname);
                            db.collection(SHARED_LEVEL)
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getContext(),"salvataggio livello  riuscito",Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                            closeDialog();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),"salvataggio livello non riuscito",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });






    }

    private void closeDialog() {
        this.dismiss();
    }
}

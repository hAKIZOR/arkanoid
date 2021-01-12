package com.caserteam.arkanoid.editor.ui_upload_check;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.R;

class AdapterLevelCreated extends ArrayAdapter<LevelCreated> {

    private Context mContext;
    private int  mResource;
    private ArrayList<LevelCreated> levelCreateds;
    private Activity activity;
    private String pathCollection;


    public AdapterLevelCreated(Context context, int resource, ArrayList<LevelCreated> levelCreateds, Activity activity,String pathCollectioon){
        super(context,resource,levelCreateds);
        this.mContext = context;
        this.mResource = resource;
        this.levelCreateds = levelCreateds;
        this.activity = activity;
        this.pathCollection = pathCollectioon;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView =layoutInflater.inflate(mResource,parent,false);

        String structure = getItem(position).getStruttura();
        String nameLevel = getItem(position).getNomeLivello();

        TextView textViewLevel = convertView.findViewById(R.id.nameLevel);
        textViewLevel.setText(nameLevel);

        ImageButton buttonEdit = (ImageButton) convertView.findViewById(R.id.editButton);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"cliccato il livello"+ nameLevel,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, EditorActivity.class);
                intent.putExtra(EditorActivity.STATE_STRUCTURE, structure);
                intent.putExtra(EditorActivity.STATE_NAME_LEVEL, nameLevel);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);

            }
        });
        ImageButton buttonRemove = (ImageButton) convertView.findViewById(R.id.removeButton);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a reference to the cities collection
                db.collection(pathCollection)
                        .whereEqualTo("nomeLivello",nameLevel)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            levelCreateds = new ArrayList<>();
                            String id = null;
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }

                            db.collection(pathCollection).document(id).delete();

                        } else {
                            Log.d("UploadLevelActivity","upload non riuscito");
                        }
                    }
                });

                if(levelCreateds != null){
                    Log.d("levelCreatedsSize",String.valueOf(levelCreateds.size()));
                    levelCreateds.remove(position);
                    AsyncTaskLoadResult.ListenerAsyncData listenerAsyncData = (AsyncTaskLoadResult.ListenerAsyncData)mContext;
                    listenerAsyncData.onDataOfLevelCreatedChange(levelCreateds);
                    notifyDataSetChanged();
                } else {
                    Log.d("levelCreatedsSize","is null");
                }



            }
        });

        return convertView;
    }
}

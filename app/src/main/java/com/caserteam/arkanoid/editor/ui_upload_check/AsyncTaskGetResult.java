package com.caserteam.arkanoid.editor.ui_upload_check;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.caserteam.arkanoid.editor.ui_plus_check.FragmentDetailBricks;

// AsyncTask<Params, Progress, Result>.
//    Params – the type (Object/primitive) you pass to the AsyncTask from .execute()
//    Progress – the type that gets passed to onProgressUpdate()
//    Result – the type returns from doInBackground()
// Any of them can be String, Integer, Void, etc.

public class AsyncTaskGetResult extends AsyncTask<Void, Integer, ArrayList<LevelCreated>> {
    private ArrayList<LevelCreated> levelCreateds;
    private ListenerAsyncData listenerAsyncData;
    private Context context;
    private UploadLevelActivity activity;
    public AsyncTaskGetResult(Context context, UploadLevelActivity activity) {
        this.context = context;
        this.activity = activity;
    }


    // Runs in UI before background thread is called
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Do something like display a progress bar
    }

    // This is run in a background thread
    @Override
    protected ArrayList<LevelCreated> doInBackground(Void ...voids) {
        // get the string from params, which is an array


        // Do something that takes a long time, for example:
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection("utenti/Davide/livelli").get();
        try {
            Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        levelCreateds = new ArrayList<>();
        for(DocumentSnapshot documentSnapshot : task.getResult()){
            levelCreateds.add(documentSnapshot.toObject(LevelCreated.class));
        }

        return levelCreateds;
    }

    // This is called from background thread but runs in UI
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        // Do things like update the progress bar
    }

    // This runs in UI when background thread finishes
    @Override
    protected void onPostExecute(ArrayList<LevelCreated> result) {
        listenerAsyncData = (ListenerAsyncData) context;
        listenerAsyncData.transferResult(result);
        super.onPostExecute(result);

        // Do things like hide the progress bar or change a TextView
    }

    public void setLevelCreateds(ArrayList<LevelCreated> levelCreateds) {
        this.levelCreateds = levelCreateds;
    }

    public ArrayList<LevelCreated> getLevelCreateds() {
        return levelCreateds;
    }
    public  interface ListenerAsyncData {
         void transferResult(ArrayList<LevelCreated> levelCreateds);
    }
}
package com.caserteam.arkanoid.editor.ui_upload_check;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// AsyncTask<Params, Progress, Result>.
//    Params – nessun parametro da passare in doInBackGround
//    Progress – nessun valore da passare a onProgressUpdate
//    Result – il risultato o il model di dati aggiornato,
//    nonchè valore di ritorno per il doInBackGround e parametro di input passato a onPostExecute
// Any of them can be String, Integer, Void, etc.

public class AsyncTaskLoadResult extends AsyncTask<Void, Void, ArrayList<LevelCreatedModel>> {
    private ArrayList<LevelCreatedModel> levelCreateds;
    private ListenerAsyncData listenerAsyncData;
    private Context context;
    private UploadLevelActivity activity;
    private String pathCollection;

    public AsyncTaskLoadResult(Context context, UploadLevelActivity activity,String pathCollection) {
        this.context = context;
        this.activity = activity;
        this.pathCollection = pathCollection;
    }


    // Runs in UI before background thread is called
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // esecuzione del thread
    @Override
    protected ArrayList<LevelCreatedModel> doInBackground(Void ...voids) {

        // attendo e effettuo il retrieving dei dati dalla collection livelli di un utente

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection(pathCollection).get();
        try {
            //aspetto appunto l'esito dell'operazione di retrieving dei dati
            Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(task.isSuccessful()){
            levelCreateds = new ArrayList<>();
            for(DocumentSnapshot documentSnapshot : task.getResult()){
                levelCreateds.add(documentSnapshot.toObject(LevelCreatedModel.class));
            }
        }

        return levelCreateds;
    }

    @Override
    protected void onProgressUpdate(Void ...voids) {
        super.onProgressUpdate(voids);
    }

    // una volta finito il thread di retrieving dei dati accedo alla thread di UI
    @Override
    protected void onPostExecute(ArrayList<LevelCreatedModel> result) {
        listenerAsyncData = (ListenerAsyncData) context;
        // attivo la chiamata a fronte della modifica nel model di dati contenuto in result
        listenerAsyncData.onDataOfLevelCreatedChange(result);
        super.onPostExecute(result);

    }

    public void setLevelCreateds(ArrayList<LevelCreatedModel> levelCreateds) {
        this.levelCreateds = levelCreateds;
    }

    public ArrayList<LevelCreatedModel> getLevelCreateds() {
        return levelCreateds;
    }
    public  interface ListenerAsyncData {
         void onDataOfLevelCreatedChange(ArrayList<LevelCreatedModel> levelCreateds);
    }
}
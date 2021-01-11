package com.caserteam.arkanoid.editor.ui_upload_check;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.zip.Inflater;

import com.caserteam.arkanoid.editor.EditorActivity;
import com.caserteam.arkanoid.R;

public class LoadingDialog {
    public static String MESSAGE_SAVE = "Sto salvando";
    public static String MESSAGE_UPLOAD_LEVEL = "caricamento livelli creati";
    public static String MESSAGE_LOAD_LEVEL = "caricamento livello selezionato";
    public static String MESSAGE_DELETE_LEVEL = "eliminazione livello in corso";
    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    public void startDialog(String message){
        //costruisco l'alert permettendo ad esso di essere visualizzato nell'activity
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //setto la View in modo da visualizzare il messaggio message posto sotto la loading bar
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_dialog,null);
        TextView textView = (TextView) view.findViewById(R.id.textLoading);
        textView.setText(message);
        builder.setView(view);
        builder.setCancelable(false);

        // mostro su schermo il dialog
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}

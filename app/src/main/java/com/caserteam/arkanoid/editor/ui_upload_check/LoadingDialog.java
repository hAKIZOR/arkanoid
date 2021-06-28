package com.caserteam.arkanoid.editor.ui_upload_check;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.caserteam.arkanoid.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;
    private TextView textLoading;
    private Button buttonCancel;
    private LoadingDialogClickListener loadingDialogClickListener;
    private Object dataToCancel;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }
    public void setDataToCancel(Object dataToCancel){
        this.dataToCancel = dataToCancel;
    }
    public void startDialog(String message){
        //costruisco l'alert permettendo ad esso di essere visualizzato nell'activity
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //setto la View in modo da visualizzare il messaggio message posto sotto la loading bar
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_dialog,null);
        textLoading = (TextView) view.findViewById(R.id.textLoading);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialogClickListener = (LoadingDialogClickListener) activity;
                loadingDialogClickListener.onClickButtonCancel(dataToCancel);
            }
        });

        textLoading.setText(message);
        builder.setView(view);
        builder.setCancelable(false);

        // mostro su schermo il dialog
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

    public  Button getButtonCancelReference (){
        return this.buttonCancel;
    }

    public void setVisibleClick(boolean visibleClick){
        if(visibleClick){
            buttonCancel.setVisibility(View.VISIBLE);
        }
    }
    public interface LoadingDialogClickListener{
        void onClickButtonCancel(Object dataToCancel);
    }
}

package com.caserteam.arkanoid.editor;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class PromptUtils extends View {
    private static Context context;

    private PromptUtils(Context context){
        super(context);
        this.context = context;
    }

    public static PromptUtils initialize(Context context){
        return new PromptUtils(context);
    }

    public void showMessage(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}

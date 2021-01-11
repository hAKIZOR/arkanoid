package com.caserteam.arkanoid.editor;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class PromptUtils extends View {
    public static final int SELECTION_MESSAGE_BRICK = 0;
    public static final int DELETE_MESSAGE_BRICK = 1;
    public static final int DELETE_ALL_MESSAGE_BRICK = 2;
    public static final int DELETE_MESSAGE_FAILURE = 3;
    public static final int DELETE_ALL_MESSAGE_FAILURE = 4;
    public static final int SAVE_FAILED_NO_BRICK_IN_THE_GRID = 5;
    public static final int SAVE_SUCCESS = 6;
    private static Context context;

    private PromptUtils(Context context){
        super(context);
        this.context = context;
    }

    public static PromptUtils initialize(Context context){
        return new PromptUtils(context);
    }
    public void showMessage(int message){
        switch (message) {
            case SELECTION_MESSAGE_BRICK:
                Toast.makeText(context,"seleziona un mattone",Toast.LENGTH_SHORT).show();
                break;
            case DELETE_MESSAGE_BRICK:
                Toast.makeText(context,"mattone selezionato eliminato",Toast.LENGTH_SHORT).show();
                break;
            case DELETE_ALL_MESSAGE_BRICK:
                Toast.makeText(context,"tutti i mattoni inseriti sono eliminati",Toast.LENGTH_SHORT).show();
                break;
            case DELETE_MESSAGE_FAILURE:
                Toast.makeText(context,"seleziona un mattone per eliminarlo",Toast.LENGTH_SHORT).show();
                break;
            case DELETE_ALL_MESSAGE_FAILURE:
                Toast.makeText(context,"inserisci dei mattoni per eliminarli",Toast.LENGTH_SHORT).show();
                break;
            case SAVE_FAILED_NO_BRICK_IN_THE_GRID:
                Toast.makeText(context,"per salvare inserisci almeno un brick nella griglia",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}

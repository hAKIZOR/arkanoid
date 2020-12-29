package com.example.arkanoid;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "dbarkanoid.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context) throws IOException {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/"+context.getPackageName() + "/databases/";
        createDataBase();
    }


    public void createDataBase(){
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            final String createDbStatement;
            final String createDbStatement2;
            try{
                createDbStatement = ResourceUtils.getRawAsString(myContext,R.raw.levels);
                DatabaseUtils.createDbFromSqlStatements(myContext,DB_NAME,10,createDbStatement);
                createDbStatement2 = ResourceUtils.getRawAsString(myContext,R.raw.insertlevels);
                openDataBase();
                myDataBase.execSQL(createDbStatement2);
                myDataBase.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("db","error creating db");
            }

        }
    }

    private boolean checkDataBase() {
        File dbFile = myContext.getDatabasePath(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            createDataBase();
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query(table, null, null, null, null, null, null);
    }


}
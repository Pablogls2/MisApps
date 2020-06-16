package com.example.rss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class BDControlador extends SQLiteOpenHelper {


    String sqlCreate = "CREATE TABLE Juegos ( id INTEGER , titulo TEXT, plataforma  TEXT , fecha DATE , precio REAL , imagen BLOB)";



    public BDControlador(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Juegos");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }
}

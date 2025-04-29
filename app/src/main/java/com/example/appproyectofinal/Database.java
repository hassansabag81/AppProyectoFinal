package com.example.appproyectofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.example.appproyectofinal.Estructura.EstructuraTabla.*;


import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ContactosVol2";
    public static final int DATABASE_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TABLE_NAME + " ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NAME+ " TEXT NOT NULL, "+ COLUMN_PHONE+ " TEXT NOT NULL);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query  = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    //METODO PARA AGREGAR UN CONTACTO
    public long agregarContacto(String nombre, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenedor = new ContentValues();
        contenedor.put(COLUMN_NAME, nombre);
        contenedor.put(COLUMN_PHONE, telefono);

        long id = db.insert(TABLE_NAME, null, contenedor);
        db.close();
        return id;
    }

    //Metodo para obtener todos los contactos
    public Cursor obtenerContactos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        //return db.rawQuery("SELECT id AS _id, nombre, telefono FROM contactos", null);
    }

    //METODO PARA  ACTUALIZAR UN CONTACTO
    public int  actualizarContactos(int id, String nombre, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenedor = new ContentValues();
        contenedor.put(COLUMN_NAME, nombre);
        contenedor.put(COLUMN_PHONE, telefono);
        int resul = db.update(TABLE_NAME, contenedor, _ID+"=?", new String[]{String.valueOf(id)});
        db.close();
        return resul;
    }

    //METODO PARA BORRAR CONTACTO
    public int eliminarContacto(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int resul = db.delete(TABLE_NAME, _ID+"=?", new String[]{String.valueOf(id)});
        db.close();
        return resul;
    }
}

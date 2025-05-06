package com.example.appproyectofinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UsuariosDB";
    private static final int DATABASE_VERSION = 1;

    // Tablas
    private static final String TABLE_USERS = "usuarios";
    private static final String TABLE_CONTACTS = "contactos_emergencia";

    // Columnas de usuarios
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_APELLIDO_MATERNO = "apellido_materno";
    private static final String KEY_DIRECCION = "direccion";
    private static final String KEY_TELEFONO = "telefono";

    // Columnas de contactos de emergencia
    private static final String KEY_CONTACT_NAME = "nombre_contacto";
    private static final String KEY_CONTACT_PHONE = "telefono_contacto";
    private static final String KEY_CONTACT_PARENTESCO = "parentesco";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOMBRE + " TEXT,"
                + KEY_APELLIDO + " TEXT,"
                + KEY_APELLIDO_MATERNO + " TEXT,"
                + KEY_DIRECCION + " TEXT,"
                + KEY_TELEFONO + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_NAME + " TEXT,"
                + KEY_CONTACT_PHONE + " TEXT,"
                + KEY_CONTACT_PARENTESCO + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas si existen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public boolean insertarUsuario(String nombre, String apellido, String apellidoMaterno, String direccion, String telefono) {
        if (!telefono.matches("^871\\d{7}$")) {
            return false; // Validación de número de Torreón
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);
        values.put(KEY_APELLIDO, apellido);
        values.put(KEY_DIRECCION, direccion);
        values.put(KEY_TELEFONO, telefono);

        if (apellidoMaterno != null && !apellidoMaterno.trim().isEmpty()) {
            values.put("apellido_materno", apellidoMaterno.trim());
        }

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    public boolean hayUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }
        cursor.close();
        return existe;
    }



    public boolean actualizarUsuario(String nombre, String apellido, String apellidoM, String direccion, String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("apellido_materno",apellidoM);
        values.put("direccion", direccion);
        values.put("telefono", telefono);
        int result = db.update("usuarios", values, "id = (SELECT id FROM usuarios LIMIT 1)", null);
        return result > 0;
    }

    public boolean insertarContacto(String nombre, String telefono, String parentesco) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_NAME, nombre);
        values.put(KEY_CONTACT_PHONE, telefono);
        values.put(KEY_CONTACT_PARENTESCO, parentesco);

        long result = db.insert(TABLE_CONTACTS, null, values);
        db.close();

        return result != -1;
    }



    public Cursor obtenerUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public Cursor obtenerContactos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
    }
}

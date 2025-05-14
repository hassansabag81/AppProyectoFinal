package com.example.appproyectofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UsuariosDB";
    private static final int DATABASE_VERSION = 3; // Incrementado por el cambio de esquema

    // Tablas
    private static final String TABLE_USERS = "usuarios";
    public static final String TABLE_CONTACTS = "contactos_emergencia";
    private static final String TABLE_ALERTS = "alertas";

    // Columnas de usuarios
    public static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_APELLIDO_MATERNO = "apellido_materno";
    private static final String KEY_DIRECCION = "direccion";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_IMAGEN_BASE64 = "imagen_base64";
    private static final String KEY_IMAGEN_PATH = "imagen_path"; // Nueva columna

    // Columnas de contactos de emergencia
    public static final String KEY_CONTACT_NAME = "nombre_contacto";
    public static final String KEY_CONTACT_PHONE = "telefono_contacto";
    public static final String KEY_CONTACT_PARENTESCO = "parentesco";

    //alertas
    private static final String KEY_ALERT_DATE = "fecha";
    private static final String KEY_ALERT_TIME = "hora";
    private static final String KEY_ALERT_LOCATION = "ubicacion";
    private static final String KEY_ALERT_LATITUDE = "latitud";
    private static final String KEY_ALERT_LONGITUDE = "longitud";

    // Tamaño máximo para imágenes en Base64 (1MB)
    private static final int MAX_IMAGE_SIZE_BYTES = 1 * 1024 * 1024;

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
                + KEY_TELEFONO + " TEXT,"
                + KEY_IMAGEN_BASE64 + " TEXT,"
                + KEY_IMAGEN_PATH + " TEXT)"; // Nueva columna
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_NAME + " TEXT,"
                + KEY_CONTACT_PHONE + " TEXT,"
                + KEY_CONTACT_PARENTESCO + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // Crear tabla de alertas (versión simplificada)
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_ALERTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ALERT_DATE + " TEXT,"
                + KEY_ALERT_TIME + " TEXT,"
                + KEY_ALERT_LOCATION + " TEXT,"
                + KEY_ALERT_LATITUDE + " REAL,"
                + KEY_ALERT_LONGITUDE + " REAL)";
        db.execSQL(CREATE_ALERTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_IMAGEN_PATH + " TEXT");
        }

        if (oldVersion < 3) {
            // Crear la tabla simplificada de alertas
            String CREATE_ALERTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALERTS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_ALERT_DATE + " TEXT,"
                    + KEY_ALERT_TIME + " TEXT,"
                    + KEY_ALERT_LOCATION + " TEXT,"
                    + KEY_ALERT_LATITUDE + " REAL,"
                    + KEY_ALERT_LONGITUDE + " REAL)";
            db.execSQL(CREATE_ALERTS_TABLE);
        }
    }

    // Métodos para usuarios
    public boolean insertarUsuario(String nombre, String apellido, String apellidoMaterno,
                                   String direccion, String telefono, String imagenBase64) {
        if (!telefono.matches("^871\\d{7}$")) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);
        values.put(KEY_APELLIDO, apellido);
        values.put(KEY_DIRECCION, direccion);
        values.put(KEY_TELEFONO, telefono);

        if (apellidoMaterno != null && !apellidoMaterno.trim().isEmpty()) {
            values.put(KEY_APELLIDO_MATERNO, apellidoMaterno.trim());
        }

        if (imagenBase64 != null && !imagenBase64.trim().isEmpty()) {
            values.put(KEY_IMAGEN_BASE64, imagenBase64.trim());
        }

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean actualizarUsuario(String nombre, String apellido, String apellidoM,
                                     String direccion, String telefono, String imagenBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);
        values.put(KEY_APELLIDO, apellido);
        values.put(KEY_APELLIDO_MATERNO, apellidoM);
        values.put(KEY_DIRECCION, direccion);
        values.put(KEY_TELEFONO, telefono);

        if (imagenBase64 != null && !imagenBase64.trim().isEmpty()) {
            values.put(KEY_IMAGEN_BASE64, imagenBase64.trim());
        }

        int result = db.update(TABLE_USERS, values, KEY_ID + " = (SELECT " + KEY_ID + " FROM " + TABLE_USERS + " LIMIT 1)", null);
        db.close();
        return result > 0;
    }

    public boolean actualizarUsuarioConImagenPath(String nombre, String apellido, String apellidoM,
                                                  String direccion, String telefono, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);
        values.put(KEY_APELLIDO, apellido);
        values.put(KEY_APELLIDO_MATERNO, apellidoM);
        values.put(KEY_DIRECCION, direccion);
        values.put(KEY_TELEFONO, telefono);
        values.put(KEY_IMAGEN_PATH, imagePath);

        int result = db.update(TABLE_USERS, values, KEY_ID + " = (SELECT " + KEY_ID + " FROM " + TABLE_USERS + " LIMIT 1)", null);
        db.close();
        return result > 0;
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

    public Cursor obtenerUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public String obtenerImagenPath() {
        SQLiteDatabase db = this.getReadableDatabase();
        String path = null;

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_IMAGEN_PATH},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            path = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return path;
    }

    // Métodos para contactos de emergencia
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

    public Cursor obtenerContactos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
    }

    // Métodos para manejo de imágenes
    public String convertirBitmapABase64(Bitmap bitmap) {
        Bitmap optimizedBitmap = optimizeBitmapSize(bitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int quality = 100;
        optimizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        // Reducir calidad si es necesario
        while (byteArrayOutputStream.toByteArray().length > MAX_IMAGE_SIZE_BYTES && quality > 30) {
            byteArrayOutputStream.reset();
            quality -= 10;
            optimizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        }

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap convertirBase64ABitmap(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("Database", "Error al convertir Base64 a Bitmap", e);
            return null;
        }
    }

    public String guardarImagenInternamente(Bitmap bitmap, Context context) {
        String filename = "user_profile_" + System.currentTimeMillis() + ".jpg";
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            return context.getFilesDir() + "/" + filename;
        } catch (IOException e) {
            Log.e("Database", "Error al guardar imagen internamente", e);
            return null;
        }
    }

    public Bitmap cargarImagenDesdePath(String imagePath) {
        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e("Database", "Error al cargar imagen desde path", e);
        }
        return null;
    }

    private Bitmap optimizeBitmapSize(Bitmap originalBitmap) {
        int maxWidth = 800; // Ancho máximo deseado
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        if (originalWidth <= maxWidth) {
            return originalBitmap;
        }

        float aspectRatio = (float) originalWidth / (float) originalHeight;
        int newHeight = (int) (maxWidth / aspectRatio);

        return Bitmap.createScaledBitmap(originalBitmap, maxWidth, newHeight, true);
    }

    public long insertarAlerta(String fecha, String hora, String ubicacion,
                               double latitud, double longitud) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ALERT_DATE, fecha);
        values.put(KEY_ALERT_TIME, hora);
        values.put(KEY_ALERT_LOCATION, ubicacion);
        values.put(KEY_ALERT_LATITUDE, latitud);
        values.put(KEY_ALERT_LONGITUDE, longitud);

        long id = db.insert(TABLE_ALERTS, null, values);
        db.close();
        return id;
    }

    public Cursor obtenerAlertas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ALERTS,
                null, // todas las columnas
                null, // sin where
                null, // sin args where
                null, // sin group by
                null, // sin having
                KEY_ALERT_DATE + " DESC, " + KEY_ALERT_TIME + " DESC"); // orden
    }

    public String obtenerNombreUsuario() {
        SQLiteDatabase db = this.getReadableDatabase();
        String nombreCompleto = "";

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_NOMBRE, KEY_APELLIDO, KEY_APELLIDO_MATERNO},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            String apellidoP = cursor.getString(1);
            String apellidoM = cursor.getString(2);

            nombreCompleto = nombre + " " + apellidoP;
            if (apellidoM != null && !apellidoM.isEmpty()) {
                nombreCompleto += " " + apellidoM;
            }
        }

        cursor.close();
        db.close();
        return nombreCompleto;
    }

    public boolean eliminarContacto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

}


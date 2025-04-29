package com.example.appproyectofinal;

import android.provider.BaseColumns;

public class Estructura {
    public Estructura(){ }

    public static abstract class EstructuraTabla implements BaseColumns{
        public static final String TABLE_NAME = "tablaContactos";
        public static final String COLUMN_NAME = "nombre";
        public static final String COLUMN_PHONE = "telefono";
    }
}

package com.example.appproyectofinal;

import android.provider.BaseColumns;

public final class Estructura {

    private Estructura() {}

    public static class Usuario implements BaseColumns {
        public static final String TABLE_NAME = "usuario";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_APELLIDO_P = "apellido_paterno";
        public static final String COLUMN_APELLIDO_M = "apellido_materno";
        public static final String COLUMN_TELEFONO = "telefono";
        public static final String COLUMN_DIRECCION = "direccion";
        public static final String COLUMN_IMAGEN = "imagen";
    }

    public static class Contacto implements BaseColumns {
        public static final String TABLE_NAME = "contacto";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_TELEFONO = "telefono";
    }

    public static class HistorialLlamadas implements BaseColumns {
        public static final String TABLE_NAME = "historial_llamadas";
        public static final String COLUMN_CONTACTO_ID = "contacto_id";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_TIPO = "tipo"; // entrante, saliente, perdida
    }
}


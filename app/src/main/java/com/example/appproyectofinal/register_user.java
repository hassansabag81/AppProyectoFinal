package com.example.appproyectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Context;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;

import android.Manifest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class register_user extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 10;
    private static final int REQUEST_CAMERA = 20;
    private Uri photoUri;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private String imagenBase64 = null;

    ImageView imagen;
    Button contacto, registrar;
    EditText nameUser, lastnameFUser, lastnameMUser, telefonoUser, direccionUser;
    CheckBox tc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        imagen = findViewById(R.id.imageView);
        contacto = findViewById(R.id.contactsBtn);
        registrar = findViewById(R.id.registrarUser);
        nameUser = findViewById(R.id.nombreUser);
        lastnameFUser = findViewById(R.id.apellidoUser);
        lastnameMUser = findViewById(R.id.apellidoUser2);
        direccionUser = findViewById(R.id.direccion);
        telefonoUser = findViewById(R.id.telefonoUser);
        tc = findViewById(R.id.termsConditions);

        Database db = new Database(this);
        if (db.hayUsuarios()) {
            Intent intent = new Intent(register_user.this, MenuActivity2.class);
            startActivity(intent);
            finish();
            return;
        }


        dialog(register_user.this);

        //Esto es para hacer que el texto te rediriga al layout de terms & confitions
        String fullText = "He leído y acepto los términos y condiciones de Sorora.";
        SpannableString ss = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                AlertDialog.Builder builder = new AlertDialog.Builder(register_user.this);
                builder.setTitle("Terminos y condiciones");
                LayoutInflater inflater = LayoutInflater.from(register_user.this);
                View dialogView = inflater.inflate(R.layout.fragment_terms2, null);
                builder.setView(dialogView);
                builder.setPositiveButton("Aceptar", null);
                builder.show();
            }
        };

        int start = fullText.indexOf("términos");
        int end = fullText.length();
        ss.setSpan(clickableSpan, start, end, 0);

        tc.setText(ss);
        tc.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String nombre = nameUser.getText().toString().trim();
                    String apellidoPaterno = lastnameFUser.getText().toString().trim();
                    String apellidoMaterno = lastnameMUser.getText().toString().trim();
                    String direccion = direccionUser.getText().toString().trim();
                    String telefono = telefonoUser.getText().toString().trim();
                    boolean termsAccepted = tc.isChecked();

                    if (nombre.isEmpty()) {
                        nameUser.setError("El nombre es obligatorio");
                        nameUser.requestFocus();
                        return;
                    }

                    if (apellidoPaterno.isEmpty()) {
                        lastnameFUser.setError("El apellido paterno es obligatorio");
                        lastnameFUser.requestFocus();
                        return;
                    }

                    if (direccion.isEmpty()) {
                        direccionUser.setError("La direccion es obligatoria");
                        direccionUser.requestFocus();
                        return;
                    }

                    if (telefono.isEmpty()) {
                        telefonoUser.setError("El teléfono es obligatorio");
                        telefonoUser.requestFocus();
                        return;
                    }

                    if (!telefono.matches("^871\\d{7}$")) {
                        telefonoUser.setError("Debe ser un número válido de Torreón (871XXXXXXX)");
                        return;
                    }

                    if (telefono.length() != 10 || !telefono.matches("[0-9]+")) {
                        telefonoUser.setError("Debe ser un número de 10 dígitos");
                        telefonoUser.requestFocus();
                        return;
                    }

                    if (!termsAccepted) {
                        Toast.makeText(register_user.this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                        return;
                    }

                Database db = new Database(register_user.this);
                boolean insertado = db.insertarUsuario(nombre, apellidoPaterno, apellidoMaterno, direccion, telefono, imagenBase64);


                if (insertado) {
                    Toast.makeText(register_user.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(register_user.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                }

                    Intent intent = new Intent(register_user.this, MenuActivity2.class);
                    startActivity(intent);
                    finish();

            }
        });


        requestAllPermissions();
    }

    private void requestAllPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.VIBRATE,
                Manifest.permission.CAMERA,
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }


    public void cargarImagen(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Seleccionar opción");

        String[] opciones = {"Tomar foto", "Seleccionar de galería"};
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                // Tomar foto
                photoUri = crearImagenUri();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (which == 1) {
                // Seleccionar de galería
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Seleccionar"), 10); // Código 10 para galería
            }
        });

        builder.show();
    }

    private void guardarImagenInterna(Uri imageUri) {
        try {
            // Convertir Uri a un archivo
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = openFileOutput("imagen_guardada.jpg", Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            // Ahora la imagen está guardada en el almacenamiento interno
            Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dialog(Context context){
        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.add_contact, null);
                builder.setTitle("Agregar contactos");
                builder.setView(dialogView);

                EditText nombreContacto = dialogView.findViewById(R.id.nombreCE);
                EditText telefonoContacto = dialogView.findViewById(R.id.telefonoCE);
                RadioGroup parentescoContacto = dialogView.findViewById(R.id.radioGroup);

                builder.setView(dialogView);
                builder.setTitle("Agregar contacto de emergencia");
                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombre = nombreContacto.getText().toString().trim();
                        String telefono = telefonoContacto.getText().toString().trim();

                        int selectedId = parentescoContacto.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            Toast.makeText(register_user.this, "Seleccione un parentesco", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String parentesco = ((android.widget.RadioButton) dialogView.findViewById(selectedId)).getText().toString();

                        if (nombre.isEmpty() || telefono.isEmpty()) {
                            Toast.makeText(register_user.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Database db = new Database(register_user.this);
                        boolean insertado = db.insertarContacto(nombre, telefono, parentesco);
                        if (insertado) {
                            Toast.makeText(register_user.this, "Contacto guardado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(register_user.this, "Error al guardar contacto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });
    }

    private Uri crearImagenUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la cámara");

        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.getData();
            } else if (requestCode == REQUEST_CAMERA) {
                imageUri = photoUri;
            }

            if (imageUri != null) {
                imagen.setImageURI(imageUri);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Database db = new Database(this);
                    imagenBase64 = db.convertirBitmapABase64(bitmap);  // Convierte a Base64 y guarda para insertarlo
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean validarCampos(EditText nombre, EditText telefono, RadioGroup parentesco) {
        if (nombre.getText().toString().trim().isEmpty() || telefono.getText().toString().trim().isEmpty() || parentesco.getCheckedRadioButtonId() == -1) {
            Toast.makeText(nombre.getContext(), "Error: falta campos por rellenar", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void contactoGuardado(){
        Database db = new Database(this);
        Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
    }
}

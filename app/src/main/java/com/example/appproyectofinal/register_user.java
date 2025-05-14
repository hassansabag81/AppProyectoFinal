package com.example.appproyectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import android.Manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class register_user extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 10;
    private static final int REQUEST_CAMERA = 20;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private String imagenBase64 = null;

    ImageView imagen;
    Button contacto, registrar;
    EditText nameUser, lastnameFUser, lastnameMUser, telefonoUser, direccionUser;
    CheckBox tc;

    private static final String PROFILE_IMAGE_NAME = "user_profile.jpg";

    private void guardarImagenLocal(Bitmap bitmap) {
        try {
            // Reducir calidad para ahorrar espacio
            FileOutputStream fos = openFileOutput(PROFILE_IMAGE_NAME, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos); // 80% de calidad
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                // Permisos concedidos, puedes proceder
            } else {
                Toast.makeText(this, "Se necesitan los permisos para cambiar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

        if (existeImagenLocal()) {
            Bitmap bitmap = cargarImagenLocal();
            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
                imagenBase64 = db.convertirBitmapABase64(bitmap);
            }
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

                if (imagenBase64 == null || imagenBase64.isEmpty()) {
                    Toast.makeText(register_user.this, "Debe seleccionar una imagen de perfil", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!existeImagenLocal()) {
                    Toast.makeText(register_user.this, "Debe seleccionar una imagen de perfil", Toast.LENGTH_SHORT).show();
                    return;
                }

                Database db = new Database(register_user.this);
                boolean insertado = db.insertarUsuario(
                        nombre,
                        apellidoPaterno,
                        apellidoMaterno,
                        direccion,
                        telefono,
                        imagenBase64 // Opcional, puedes pasar null si ya no lo necesitas
                );

                if (insertado) {
                    Toast.makeText(register_user.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register_user.this, MenuActivity2.class));
                    finish();
                } else {
                    Toast.makeText(register_user.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                }
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
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        // Verificar permisos primero
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_CODE);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen de perfil");

        String[] opciones = {"Tomar foto", "Elegir de galería"};
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) { // Tomar foto
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Versión simplificada que funciona en la mayoría de dispositivos
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                }
            } else { // Elegir de galería
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_GALLERY);
            }
        });
        builder.show();
    }

    private Bitmap cargarImagenLocal() {
        try {
            FileInputStream fis = openFileInput(PROFILE_IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean existeImagenLocal() {
        File file = new File(getFilesDir(), PROFILE_IMAGE_NAME);
        return file.exists();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            try {
                if (requestCode == REQUEST_GALLERY && data != null) {
                    // Para galería
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        bitmap = BitmapFactory.decodeFile(picturePath);
                    }
                } else if (requestCode == REQUEST_CAMERA && data != null) {
                    // Para cámara - versión simplificada pero funcional
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");

                    // Rotar la imagen si es necesario (opcional)
                    //bitmap = rotarImagenSiNecesario(bitmap);
                }

                if (bitmap != null) {
                    // Redimensionar para evitar problemas de memoria
                    bitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true);

                    imagen.setImageBitmap(bitmap);

                    // Guardar localmente
                    guardarImagenLocal(bitmap);

                    // Convertir a Base64 para la base de datos
                    Database db = new Database(this);
                    imagenBase64 = db.convertirBitmapABase64(bitmap);

                    Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap rotarImagenSiNecesario(Bitmap bitmap) {
        // Esta es una implementación básica, puedes mejorarla según tus necesidades
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // Rotar 90 grados (ajusta según necesidad)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
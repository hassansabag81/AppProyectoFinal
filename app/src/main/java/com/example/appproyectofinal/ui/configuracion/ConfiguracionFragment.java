package com.example.appproyectofinal.ui.configuracion;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.appproyectofinal.Database;
import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentConfiguracionBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ConfiguracionFragment extends Fragment {

    private static final int REQUEST_GALLERY = 10;
    private static final int REQUEST_CAMERA = 20;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private Database database;
    private FragmentConfiguracionBinding binding;
    private Uri photoUri;
    private String imagenBase64 = null;

    public ConfiguracionFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false);

        database = new Database(getContext());
        verificarPermisos();

        // Cargar imagen desde la base de datos
        cargarImagenUsuario();

        // Cargar datos del usuario
        cargarDatosUsuario();

        binding.contactsBtn.setOnClickListener(v -> {
            DialogoNewContact();
        });

        binding.imgNewBtn.setOnClickListener(view -> {
            mostrarDialogoSeleccionImagen();
        });

        return binding.getRoot();
    }

    private void cargarImagenUsuario() {
        if (database.hayUsuarios()) {
            Cursor cursor = database.obtenerUsuarios();
            if (cursor.moveToFirst()) {
                int imagenColumnIndex = cursor.getColumnIndex("imagen");
                if (imagenColumnIndex != -1) {
                    imagenBase64 = cursor.getString(imagenColumnIndex);
                    if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                        Bitmap bitmap = convertirBase64ABitmap(imagenBase64);
                        binding.imageView.setImageBitmap(bitmap);
                    } else {
                        binding.imageView.setImageResource(R.drawable.sorora);
                    }
                }
            }
            cursor.close();
        }
    }

    private void mostrarDialogoSeleccionImagen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
                startActivityForResult(Intent.createChooser(intent, "Seleccionar"), REQUEST_GALLERY);
            }
        });
        builder.show();
    }

    private Uri crearImagenUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la cámara");
        return requireActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == REQUEST_GALLERY && data != null) {
                imageUri = data.getData();
            } else if (requestCode == REQUEST_CAMERA) {
                imageUri = photoUri;
            }

            if (imageUri != null) {
                binding.imageView.setImageURI(imageUri);
                // Guardar solo la URI como string en la base de datos
                String uriString = imageUri.toString();
                // Actualizar la base de datos con la URI
                actualizarURIImagenEnDB(uriString);
            }
        }
    }

    private void actualizarURIImagenEnDB(String uriString) {
        // Implementa este método según tu estructura de base de datos
        // Ejemplo: database.actualizarURIImagenUsuario(uriString);
    }

    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap convertirBase64ABitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisos concedidos
            } else {
                Toast.makeText(getContext(), "Se necesitan los permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarDatosUsuario() {
        if (database.hayUsuarios()) {
            Cursor cursor = database.obtenerUsuarios();
            if (cursor.moveToFirst()) {
                // Imprimir los nombres de las columnas para depuración
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("Database", "Columna " + i + ": " + cursor.getColumnName(i));
                }

                // Asegúrate de que las columnas "nombre", "apellido", "direccion", "telefono" existan en la base de datos
                int nombreColumnIndex = cursor.getColumnIndex("nombre");
                if (nombreColumnIndex != -1) {
                    String nombre = cursor.getString(nombreColumnIndex);
                    binding.nombreUser.setText(nombre);
                } else {
                    Log.e("Database", "Columna 'nombre' no encontrada");
                }

                int apellidoColumnIndex = cursor.getColumnIndex("apellido");
                if (apellidoColumnIndex != -1) {
                    String apellido = cursor.getString(apellidoColumnIndex);
                    binding.apellidoUser.setText(apellido);
                }

                int apellidoMColumnIndex = cursor.getColumnIndex("apellido_materno");
                if (apellidoMColumnIndex != -1) {
                    String apellidoMaterno = cursor.getString(apellidoMColumnIndex);
                    binding.apellidoUser2.setText(apellidoMaterno);
                }

                int direccionColumnIndex = cursor.getColumnIndex("direccion");
                if (direccionColumnIndex != -1) {
                    String direccion = cursor.getString(direccionColumnIndex);
                    binding.direccionUser.setText(direccion);
                }

                int telefonoColumnIndex = cursor.getColumnIndex("telefono");
                if (telefonoColumnIndex != -1) {
                    String telefono = cursor.getString(telefonoColumnIndex);
                    binding.telefonoUser.setText(telefono);
                }
            }
            cursor.close();
        } else {
            Toast.makeText(getContext(), "No hay usuarios registrados", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String nombreTF = binding.nombreUser.getText().toString().trim();
        String apellidoPaternoTF = binding.apellidoUser.getText().toString().trim();
        String direccionTF = binding.direccionUser.getText().toString().trim();
        String telefonoTF = binding.telefonoUser.getText().toString().trim();

        if (nombreTF.isEmpty()) {
            binding.nombreUser.setError("El nombre es obligatorio");
            binding.nombreUser.requestFocus();
            return false;
        }

        if (apellidoPaternoTF.isEmpty()) {
            binding.apellidoUser.setError("El apellido paterno es obligatorio");
            binding.apellidoUser.requestFocus();
            return false;
        }

        if (direccionTF.isEmpty()) {
            binding.direccionUser.setError("La direccion es obligatoria");
            binding.direccionUser.requestFocus();
            return false;
        }

        if (telefonoTF.isEmpty()) {
            binding.telefonoUser.setError("El teléfono es obligatorio");
            binding.telefonoUser.requestFocus();
            return false;
        }

        if (!telefonoTF.matches("^871\\d{7}$")) {
            binding.telefonoUser.setError("Debe ser un número válido de Torreón (871XXXXXXX)");
            return false;
        }

        if (telefonoTF.length() != 10 || !telefonoTF.matches("[0-9]+")) {
            binding.telefonoUser.setError("Debe ser un número de 10 dígitos");
            binding.telefonoUser.requestFocus();
            return false;
        }

        if (item.getItemId() == R.id.action_guardar) {
            String nombre = binding.nombreUser.getText().toString();
            String apellido = binding.apellidoUser.getText().toString();
            String apellidoM = binding.apellidoUser2.getText().toString();
            String direccion = binding.direccionUser.getText().toString();
            String telefono = binding.telefonoUser.getText().toString();

            boolean exito;
            if (database.hayUsuarios()) {
                exito = database.actualizarUsuario(nombre, apellido, apellidoM, direccion, telefono, imagenBase64);
                Toast.makeText(getContext(), exito ? "Datos actualizados" : "Error al actualizar", Toast.LENGTH_SHORT).show();
            } else {
                exito = database.insertarUsuario(nombre, apellido, apellidoM, direccion, telefono, imagenBase64);
                Toast.makeText(getContext(), exito ? "Usuario guardado" : "Error al guardar", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DialogoNewContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_contact, null);
        builder.setTitle("Agregar contacto");
        builder.setView(dialogView);

        EditText nombreContacto = dialogView.findViewById(R.id.nombreCE);
        EditText telefonoContacto = dialogView.findViewById(R.id.telefonoCE);
        RadioGroup parentescoContacto = dialogView.findViewById(R.id.radioGroup);

        builder.setPositiveButton("Guardar Contacto", null);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (validarCampos(nombreContacto, telefonoContacto, parentescoContacto)) {
                String nombre = nombreContacto.getText().toString().trim();
                String telefono = telefonoContacto.getText().toString().trim();
                String parentesco = "";

                // Insertar el contacto en la base de datos
                boolean insertado = database.insertarContacto(nombre, telefono, parentesco);
                if (insertado) {
                    Toast.makeText(getContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Error al guardar contacto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error: Falta campos por rellenar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos(EditText nombre, EditText telefono, RadioGroup parentesco) {
        if (nombre.getText().toString().trim().isEmpty()){
            nombre.setError("Error: Escribe el nombre del contacto");
            return false;
        }

        if (telefono.getText().toString().trim().isEmpty()) {
            telefono.setError("Error: Escribe un numero de telefono");
            return false;
        }
        if (!telefono.getText().toString().matches("^871\\d{7}$")){
            telefono.setError("Debe ser un número válido de Torreón (871XXXXXXX)");
            return false;
        }
        if (telefono.getText().toString().length() != 10 || !telefono.getText().toString().matches("[0-9]+")) {
            telefono.setError("Debe ser un número de 10 dígitos");
            telefono.requestFocus();
            return false;
        }

        if(parentesco.getCheckedRadioButtonId() == -1){
            Toast.makeText(getContext(), "Error: Elige el parentesco", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void contactoGuardado(){
        Toast.makeText(getContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
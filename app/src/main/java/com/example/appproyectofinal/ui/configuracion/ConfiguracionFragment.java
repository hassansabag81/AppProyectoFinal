package com.example.appproyectofinal.ui.configuracion;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import androidx.navigation.Navigation;

import com.example.appproyectofinal.Database;
import com.example.appproyectofinal.MenuActivity2;
import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentConfiguracionBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

    private static final String PROFILE_IMAGE_NAME = "user_profile.jpg";

    private void guardarImagenLocal(Bitmap bitmap) {
        try {
            FileOutputStream fos = requireContext().openFileOutput(PROFILE_IMAGE_NAME, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap cargarImagenLocal() {
        try {
            FileInputStream fis = requireContext().openFileInput(PROFILE_IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
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

        binding.mostrarContactos.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_configuracion_to_contactosFragment);
        });

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
        Bitmap bitmap = cargarImagenLocal();
        if (bitmap != null) {
            binding.imageView.setImageBitmap(bitmap);
        } else {
            binding.imageView.setImageResource(R.drawable.sorora);

            // Opcional: Cargar desde base de datos si existe (para compatibilidad)
            if (database.hayUsuarios()) {
                Cursor cursor = database.obtenerUsuarios();
                if (cursor.moveToFirst()) {
                    int imagenIndex = cursor.getColumnIndex("imagen");
                    if (imagenIndex != -1) {
                        String base64 = cursor.getString(imagenIndex);
                        if (base64 != null && !base64.isEmpty()) {
                            bitmap = convertirBase64ABitmap(base64);
                            if (bitmap != null) {
                                binding.imageView.setImageBitmap(bitmap);
                                guardarImagenLocal(bitmap); // Migrar a almacenamiento local
                            }
                        }
                    }
                }
                cursor.close();
            }
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
            Bitmap bitmap = null;

            try {
                if (requestCode == REQUEST_GALLERY && data != null) {
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                } else if (requestCode == REQUEST_CAMERA) {
                    if (photoUri != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
                    }
                }

                if (bitmap != null) {
                    bitmap = redimensionarBitmap(bitmap, 800, 800);
                    binding.imageView.setImageBitmap(bitmap);
                    guardarImagenLocal(bitmap);

                    imagenBase64 = convertirBitmapABase64(bitmap);
                    Toast.makeText(getContext(), "Imagen actualizada", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace(); // Para depuración
                Toast.makeText(getContext(), "Error al procesar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > maxWidth || height > maxHeight) {
            float ratio = Math.min((float)maxWidth/width, (float)maxHeight/height);
            width = Math.round(width * ratio);
            height = Math.round(height * ratio);
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return bitmap;
    }

    private String convertirBitmapABase64(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap convertirBase64ABitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                // Si necesitas mantener la imagen en la base de datos
                exito = database.actualizarUsuario(nombre, apellido, apellidoM, direccion, telefono, imagenBase64);
                if (exito) {
                    Toast.makeText(getContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                    ((MenuActivity2) getActivity()).recreate();
                }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
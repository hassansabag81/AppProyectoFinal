package com.example.appproyectofinal.ui.configuracion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.appproyectofinal.Database;
import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentConfiguracionBinding;

import android.util.Log;

public class ConfiguracionFragment extends Fragment {

    private Database database;

    private FragmentConfiguracionBinding binding;

    public ConfiguracionFragment() {
        // Constructor público vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        database = new Database(getContext());

        binding.contactsBtn.setOnClickListener(v -> {
            DialogoNewContact();
        });

        cargarDatosUsuario();



        binding.imgNewBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, "Seleccionar"),10);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Método para cargar los datos del usuario
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

                    // Repite esto para las demás columnas
                    int apellidoColumnIndex = cursor.getColumnIndex("apellido");
                    if (apellidoColumnIndex != -1) {
                        String apellido = cursor.getString(apellidoColumnIndex);
                        binding.apellidoUser.setText(apellido);
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
                cursor.close();
            }
        } else {
            Toast.makeText(getContext(), "No hay usuarios registrados", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para guardar los cambios en los campos
    private void guardarDatosUsuario() {
        String nombre = binding.nombreUser.getText().toString();
        String apellido = binding.apellidoUser.getText().toString();
        String direccion = binding.direccionUser.getText().toString();
        String telefono = binding.telefonoUser.getText().toString();

        if (database.insertarUsuario(nombre, apellido, direccion, telefono)) {
            Toast.makeText(getContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    // Llamado cuando se presiona el botón de guardar

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_guardar) {
            String nombre = binding.nombreUser.getText().toString();
            String apellido = binding.apellidoUser.getText().toString();
            String direccion = binding.direccionUser.getText().toString();
            String telefono = binding.telefonoUser.getText().toString();

            boolean exito;
            if (database.hayUsuarios()) {
                exito = database.actualizarUsuario(nombre, apellido, direccion, telefono);
                Toast.makeText(getContext(), exito ? "Datos actualizados" : "Error al actualizar", Toast.LENGTH_SHORT).show();
            } else {
                exito = database.insertarUsuario(nombre, apellido, direccion, telefono);
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
                contactoGuardado();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Error: Falta campos por rellenar", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validarCampos(EditText nombre, EditText telefono, RadioGroup parentesco) {
        if (nombre.getText().toString().trim().isEmpty() || telefono.getText().toString().trim().isEmpty() || parentesco.getCheckedRadioButtonId() == -1) {
            Toast.makeText(nombre.getContext(), "Error: falta campos por rellenar", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void contactoGuardado(){
        Toast.makeText(getContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
    }

}

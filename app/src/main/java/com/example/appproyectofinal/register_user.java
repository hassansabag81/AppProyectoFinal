package com.example.appproyectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Context;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class register_user extends AppCompatActivity {
    ImageView imagen;
    Button contacto, registrar;
    EditText nameUser, lastnameUser, telefonoUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        imagen = (ImageView) findViewById(R.id.imageView);
        contacto = (Button) findViewById(R.id.contactsBtn);
        registrar = (Button)findViewById(R.id.registrarUser);
        nameUser = (EditText) findViewById(R.id.nombreUser);
        lastnameUser = (EditText) findViewById(R.id.apellidoUser);
        telefonoUser = (EditText) findViewById(R.id.telefonoUser);
        dialog(register_user.this);


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaRegistro(nameUser, lastnameUser, telefonoUser)){
                    Intent intent = new Intent(register_user.this, MenuActivity2.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(register_user.this, "Error: Falta campos por rellenar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validaRegistro(EditText nameUser, EditText lastnameUser, EditText telefonoUser) {
        if(nameUser.getText().toString().isEmpty() || lastnameUser.getText().toString().isEmpty() || telefonoUser.getText().toString().isEmpty()){
            Toast.makeText(this, "Error: Faltan campos por rellenar", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void cargarImagen(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccionar"),10);
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


                builder.setPositiveButton("Guardar Contacto", null);

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validarCampos(nombreContacto, telefonoContacto, parentescoContacto)) {
                            contactoGuardado();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Error: Falta campos por rellenar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri path = data.getData();
            imagen.setImageURI(path);
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
        Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
    }
}

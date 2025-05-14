package com.example.appproyectofinal;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appproyectofinal.ui.alertas.AlertasFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appproyectofinal.databinding.ActivityMenu2Binding;

import java.io.FileInputStream;

public class MenuActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenu2Binding binding;

    private static final String PROFILE_IMAGE_NAME = "user_profile.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMenu2Binding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMenu2.toolbar);

        // Obtener el nombre de la base de datos
        Database dbHelper = new Database(this);
        String nombreUsuario = dbHelper.obtenerNombreUsuario();
        dbHelper.close();

        // Asignar el nombre al TextView del encabezado
        NavigationView navigationView = binding.navView;
        navigationView.post(() -> {
            View headerView = navigationView.getHeaderView(0);
            TextView tvNombre = headerView.findViewById(R.id.tagNombre);
            tvNombre.setText(nombreUsuario);
        });

        View headerView = navigationView.getHeaderView(0);

        ImageView profileImageView = headerView.findViewById(R.id.imageView);
        Bitmap profileBitmap = cargarImagenLocal();
        if (profileBitmap != null) {
            profileImageView.setImageBitmap(profileBitmap);
        }

        DrawerLayout drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private Bitmap cargarImagenLocal() {
        try {
            FileInputStream fis = openFileInput("user_profile.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.termsConditions){
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity2.this);
            builder.setTitle("Terminos y condiciones");
            LayoutInflater inflater = LayoutInflater.from(MenuActivity2.this);
            View dialogView = inflater.inflate(R.layout.fragment_terms2, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Cerrar", null);
            builder.show();
            return true;
        }else if(id == R.id.privacity){
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity2.this);
            builder.setTitle("Aviso de Privacidad");
            LayoutInflater inflater = LayoutInflater.from(MenuActivity2.this);
            View dialogView = inflater.inflate(R.layout.fragment_privacity, null);
            builder.setView(dialogView);
            builder.setPositiveButton("Cerrar", null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosUsuario();
    }

    private void actualizarDatosUsuario() {
        // Obtener el nombre actualizado
        Database dbHelper = new Database(this);
        String nombreUsuario = dbHelper.obtenerNombreUsuario();
        dbHelper.close();

        // Actualizar UI en el encabezado
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        TextView tvNombre = headerView.findViewById(R.id.tagNombre);
        if (tvNombre != null) {
            tvNombre.setText(nombreUsuario);
        }

        ImageView profileImageView = headerView.findViewById(R.id.imageView);
        Bitmap profileBitmap = cargarImagenLocal();
        if (profileBitmap != null) {
            profileImageView.setImageBitmap(profileBitmap);
        }
    }


}
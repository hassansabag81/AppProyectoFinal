package com.example.appproyectofinal.ui.configuracion;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appproyectofinal.ui.configuracion.ConfiguracionFragment;

public class ConfiguracionController {
    private final FragmentManager fragmentManager;
    private final int containerViewId;

    public ConfiguracionController(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    // Método para mostrar el fragmento
    public void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new ConfiguracionFragment();
        transaction.replace(containerViewId, fragment, "Configuracion_FRAGMENT");
        transaction.addToBackStack(null); // Opcional: para que el usuario pueda retroceder
        transaction.commit();
    }

    // Método para destruir el fragmento
    public void destroyFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("Configuracion_FRAGMENT");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}

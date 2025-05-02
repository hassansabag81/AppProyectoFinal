package com.example.appproyectofinal.ui.alertas;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appproyectofinal.ui.alertas.AlertasFragment;

public class AlertasController {
    private final FragmentManager fragmentManager;
    private final int containerViewId;

    public AlertasController(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    // Método para mostrar el fragmento
    public void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new AlertasFragment();
        transaction.replace(containerViewId, fragment, "Alertas_FRAGMENT");
        transaction.addToBackStack(null); // Opcional: para que el usuario pueda retroceder
        transaction.commit();
    }

    // Método para destruir el fragmento
    public void destroyFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("Alertas_FRAGMENT");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}

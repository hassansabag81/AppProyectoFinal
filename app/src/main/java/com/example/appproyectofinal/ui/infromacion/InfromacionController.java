package com.example.appproyectofinal.ui.infromacion;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appproyectofinal.ui.infromacion.InformacionFragment;

public class InfromacionController {
    private final FragmentManager fragmentManager;
    private final int containerViewId;

    public InfromacionController(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    // Método para mostrar el fragmento
    public void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new InformacionFragment();
        transaction.replace(containerViewId, fragment, "Informacion_FRAGMENT");
        transaction.addToBackStack(null); // Opcional: para que el usuario pueda retroceder
        transaction.commit();
    }

    // Método para destruir el fragmento
    public void destroyFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("Informacion_FRAGMENT");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}

package com.example.appproyectofinal.ui.violentrometro;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appproyectofinal.ui.violentrometro.ViolentrometroFragment;

public class ViolentrometroController {

    private final FragmentManager fragmentManager;
    private final int containerViewId;

    public ViolentrometroController(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    // Método para mostrar el fragmento
    public void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new ViolentrometroFragment();
        transaction.replace(containerViewId, fragment, "VIOLENTROMETRO_FRAGMENT");
        transaction.addToBackStack(null); // Opcional: para que el usuario pueda retroceder
        transaction.commit();
    }

    // Método para destruir el fragmento
    public void destroyFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("VIOLENTROMETRO_FRAGMENT");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}

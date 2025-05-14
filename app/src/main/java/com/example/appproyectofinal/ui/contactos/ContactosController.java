package com.example.appproyectofinal.ui.contactos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ContactosController {
    private final FragmentManager fragmentManager;
    private final int containerViewId;

    public ContactosController(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    // Método para mostrar el fragmento
    public void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new ContactosFragment();
        transaction.replace(containerViewId, fragment, "Contactos_FRAGMENT");
        transaction.addToBackStack(null); // Opcional: para que el usuario pueda retroceder
        transaction.commit();
    }

    // Método para destruir el fragmento
    public void destroyFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("Contactos_FRAGMENT");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}

package com.example.appproyectofinal.ui.contactos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproyectofinal.Database;
import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentContactosBinding;

public class ContactosFragment extends Fragment {

    private FragmentContactosBinding binding;
    private Database database;
    private ContactosAdapter adapter;

    public ContactosFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentContactosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar la base de datos
        database = new Database(requireActivity());

        // Configurar RecyclerView
        binding.recyclerViewContactos.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Cargar los contactos
        cargarContactos();

    }

    private void cargarContactos() {
        Cursor cursor = database.obtenerContactos();
        adapter = new ContactosAdapter(cursor);
        binding.recyclerViewContactos.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (database != null) {
            database.close();
        }
        binding = null;
    }

    // Clase Adapter interna
    private class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder> {

        private Cursor cursor;

        public ContactosAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @NonNull
        @Override
        public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contacto, parent, false);
            return new ContactoViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
            if (cursor.moveToPosition(position)) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(Database.KEY_CONTACT_NAME));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(Database.KEY_CONTACT_PHONE));
                String parentesco = cursor.getString(cursor.getColumnIndexOrThrow(Database.KEY_CONTACT_PARENTESCO));

                holder.tvNombre.setText(nombre);
                holder.tvTelefono.setText(telefono);
                holder.tvParentesco.setText(parentesco);
            }
        }

        @Override
        public int getItemCount() {
            return cursor != null ? cursor.getCount() : 0;
        }

        class ContactoViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvTelefono, tvParentesco;

            public ContactoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreContacto);
                tvTelefono = itemView.findViewById(R.id.tvTelefonoContacto);
                tvParentesco = itemView.findViewById(R.id.tvParentesco);
            }
        }
    }
}
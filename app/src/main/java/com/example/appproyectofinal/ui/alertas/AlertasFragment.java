package com.example.appproyectofinal.ui.alertas;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appproyectofinal.Database;
import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentAlertasBinding;
import com.example.appproyectofinal.databinding.ItemhistorialBinding;
import java.util.ArrayList;
import java.util.List;

public class AlertasFragment extends Fragment {

    private FragmentAlertasBinding binding;
    private AlertasAdapter adapter;
    private Database database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlertasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar la base de datos
        database = new Database(getContext());

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewAlertas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AlertasAdapter();
        recyclerView.setAdapter(adapter);

        // Cargar datos
        loadAlertData();

        return root;
    }

    private void loadAlertData() {
        List<AlertaItem> alertaItems = new ArrayList<>();
        Cursor cursor = database.obtenerAlertas();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"));
                double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"));
                double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"));

                alertaItems.add(new AlertaItem(fecha, hora, ubicacion, latitud, longitud));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.setAlertaItems(alertaItems);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Clase Adapter para el RecyclerView
    private class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.AlertaViewHolder> {

        private List<AlertaItem> alertaItems = new ArrayList<>();

        public void setAlertaItems(List<AlertaItem> alertaItems) {
            this.alertaItems = alertaItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AlertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemhistorialBinding itemBinding = ItemhistorialBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new AlertaViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull AlertaViewHolder holder, int position) {
            AlertaItem item = alertaItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return alertaItems.size();
        }

        // ViewHolder
        class AlertaViewHolder extends RecyclerView.ViewHolder {
            private final ItemhistorialBinding binding;

            AlertaViewHolder(ItemhistorialBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(AlertaItem item) {
                binding.callTime.setText(item.getFecha() + " " + item.getHora());
                binding.location.setText(item.getUbicacion());
                binding.latitude.setText("lat: " + item.latitud);
                binding.longitude.setText("lon: " +item.longitud);

                // Puedes agregar más lógica aquí, como manejar clics en los items
            }
        }
    }

    // Clase modelo para los items de alerta
    private static class AlertaItem {
        private final String fecha;
        private final String hora;
        private final String ubicacion;
        private final double latitud;
        private final double longitud;

        public AlertaItem(String fecha, String hora, String ubicacion, double latitud, double longitud) {
            this.fecha = fecha;
            this.hora = hora;
            this.ubicacion = ubicacion;
            this.latitud = latitud;
            this.longitud = longitud;
        }

        // Getters
        public String getFecha() { return fecha; }
        public String getHora() { return hora; }
        public String getUbicacion() { return ubicacion; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
    }
}
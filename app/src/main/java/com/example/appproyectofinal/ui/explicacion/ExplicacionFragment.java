package com.example.appproyectofinal.ui.explicacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appproyectofinal.R;

public class ExplicacionFragment extends Fragment {

    private TextView texto;
    private ImageView imagen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explicacion, container, false);

        texto = view.findViewById(R.id.textoDetalle);
        imagen = view.findViewById(R.id.imagenDetalle);

        // Recuperar los valores del Bundle
        Bundle args = getArguments();
        if (args != null) {
            String mensaje = args.getString("texto");
            int imagenResId = args.getInt("imagenResId");

            texto.setText(mensaje);
            imagen.setImageResource(imagenResId);
        }

        return view;
    }
}
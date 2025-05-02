package com.example.appproyectofinal.ui.infromacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.appproyectofinal.R;
import com.example.appproyectofinal.databinding.FragmentInformacionBinding;

public class InformacionFragment extends Fragment {

    private FragmentInformacionBinding binding;

    public InformacionFragment() {
        // Constructor público vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInformacionBinding.inflate(inflater, container, false);

        binding.violentometroButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_informacion_to_violentometro2)
        );

        binding.violenciaButton.setOnClickListener(v -> {
            // Crear un Bundle para pasar los datos al siguiente fragmento
            Bundle bundle = new Bundle();
            bundle.putString("texto",
                    "Tipos de violencia:\n\n" +
                            "1. Violencia física:\n" +
                            "- Golpes, empujones, patadas, jalones, quemaduras.\n\n" +
                            "2. Violencia psicológica o emocional:\n" +
                            "- Humillaciones, insultos, amenazas, chantajes, manipulación.\n\n" +
                            "3. Violencia sexual:\n" +
                            "- Relaciones forzadas, tocamientos sin consentimiento, acoso sexual.\n\n" +
                            "4. Violencia económica:\n" +
                            "- Control del dinero, impedir que trabajes, quitarte tu salario.\n\n" +
                            "5. Violencia digital:\n" +
                            "- Espiar tu celular, controlar tus redes, difundir imágenes sin permiso.\n\n" +
                            "6. Violencia simbólica:\n" +
                            "- Mensajes, imágenes o actitudes que refuerzan desigualdad y estereotipos.");
            bundle.putInt("imagenResId", R.drawable.tiposv);

            // Realizar la navegación al fragmento Explicacion, pasando el Bundle
            Navigation.findNavController(v).navigate(R.id.action_informacion_to_explicacionFragment, bundle);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

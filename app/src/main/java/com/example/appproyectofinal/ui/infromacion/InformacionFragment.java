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

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

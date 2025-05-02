package com.example.appproyectofinal.ui.camara;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.appproyectofinal.databinding.FragmentCamaraBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import androidx.core.content.FileProvider;


public class CamaraFragment extends Fragment {

    private FragmentCamaraBinding binding;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Bitmap ultimaFotoBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCamaraBinding.inflate(inflater, container, false);

        // Registrar el launcher para tomar la foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ultimaFotoBitmap = imageBitmap; // Guardar la imagen para compartirla
                        binding.imgFoto.setImageBitmap(imageBitmap);
                        binding.enviarButton.setEnabled(true);
                    }
                });

        // Botón para tomar foto
        binding.fotoButton.setOnClickListener(v -> tomarFoto());

        // Botón para compartir
        binding.enviarButton.setOnClickListener(v -> {
            if (ultimaFotoBitmap != null) {
                compartirImagen(ultimaFotoBitmap);
            }
        });

        // Al inicio, el botón de enviar está desactivado
        binding.enviarButton.setEnabled(false);

        return binding.getRoot();
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private Uri guardarImagen(Bitmap bitmap) {
        try {
            File archivo = new File(requireContext().getCacheDir(), "foto_compartir.png");
            FileOutputStream fos = new FileOutputStream(archivo);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    archivo
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void compartirImagen(Bitmap bitmap) {
        Uri uri = guardarImagen(bitmap);
        if (uri == null) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Me encuentro en problemas, te envío una foto de mi ubicación 📸");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir imagen con..."));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

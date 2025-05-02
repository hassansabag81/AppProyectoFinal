package com.example.appproyectofinal.ui.camara;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.Manifest;
import android.widget.Toast;


public class CamaraFragment extends Fragment {

    private FragmentCamaraBinding binding;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Bitmap ultimaFotoBitmap;

    private FusedLocationProviderClient fusedLocationClient;


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void compartirImagen(Bitmap bitmap) {
        // Obtener la ubicación
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    String loc = "";
                    if (location != null) {
                        loc = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                    } else {
                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setNumUpdates(1);

                        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location updatedLocation = locationResult.getLastLocation();
                                if (updatedLocation != null) {
                                    String loc = "Lat: " + updatedLocation.getLatitude() + ", Lon: " + updatedLocation.getLongitude();
                                } else {
                                    Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, Looper.getMainLooper());
                    }

                    // Guardar la imagen y enviar el mensaje
                    Uri uri = guardarImagen(bitmap);
                    if (uri == null) return;

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT, "Me encuentro en problemas, te envío una foto de mi ubicación 📸\n" + loc);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Compartir imagen con..."));
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

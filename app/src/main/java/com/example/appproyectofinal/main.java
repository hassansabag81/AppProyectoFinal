package com.example.appproyectofinal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.Manifest;





import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main extends Fragment {

    private ImageButton botonAlerta;

    private static final int LONG_PRESS_TIME = 3000;  // 3 segundos
    private Handler handler = new Handler();
    private int pressDuration = 0;
    private Vibrator vibrator;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static main newInstance(String param1, String param2) {
        main fragment = new main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        Button call911Button = view.findViewById(R.id.call911);
        Button callSPButton = view.findViewById(R.id.callSP);

        call911Button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:911"));
            startActivity(intent);
        });

        callSPButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:8717290099"));
            startActivity(intent);
        });
        botonAlerta = view.findViewById(R.id.botonAlerta);

        botonAlerta.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Comienza a contar cuando se presiona el botón
                        pressDuration = 0;
                        handler.postDelayed(updateTimer, 1000);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Detiene el contador cuando el botón se suelta
                        handler.removeCallbacks(updateTimer);
                        if (pressDuration >= 3) {
                            // Si se presionó por 3 segundos, realiza la acción de emergencia
                            sendEmergencyNotification();
                            sendLocationToEmergencyContacts();
                        }
                        return true;
                }
                return false;
            }
        });

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // La ubicación está desactivada
            new AlertDialog.Builder(getContext())
                    .setTitle("Ubicación desactivada")
                    .setMessage("Por favor, activa la ubicación para continuar.")
                    .setPositiveButton("Activar", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        return view;
    }

    // Runnable para contar el tiempo que se presiona el botón
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            pressDuration++;
            Toast.makeText(getContext(), "Tiempo presionado: " + pressDuration + " segundos", Toast.LENGTH_SHORT).show();
            if (pressDuration < 3) {
                if (vibrator != null) {
                    vibrator.vibrate(100); // Vibrar por 100ms
                }
                // Continuar actualizando cada segundo
                handler.postDelayed(this, 1000);
            }
        }
    };

    // Método para emitir una notificación de emergencia
    private void sendEmergencyNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("emergency", "Emergencia", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación
        Notification notification = new NotificationCompat.Builder(getContext(), "emergency")
                .setContentTitle("¡Emergencia!")
                .setContentText("¡Alerta activada! Se está enviando tu ubicación y mensaje de emergencia.")
                .setSmallIcon(R.drawable.helper)
                .build();

        // Enviar la notificación
        notificationManager.notify(1, notification);
    }

    // Método para obtener la ubicación y enviarla a los contactos de emergencia
    private void sendLocationToEmergencyContacts() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        String loc = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                        sendSmsToEmergencyContact("8713789035", loc);
                    } else {
                        // Si es null, pedimos actualización activa
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
                                    sendSmsToEmergencyContact("8713789035", loc);
                                } else {
                                    Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, Looper.getMainLooper());
                    }
                });
    }

    // Método para enviar un SMS (simulado) a un número de emergencia con la ubicación
    private void sendSmsToEmergencyContact(String phoneNumber, String location) {
        String message = "Estoy en una emergencia, mi ubicación es: " + location;

        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));

        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }
}
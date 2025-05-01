package com.example.appproyectofinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Vibrator;
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
                        handler.postDelayed(updateTimer, 1000);  // Actualiza cada segundo
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

        // Crear canal de notificación para Android 8.0 o superior
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
        // Aquí debes obtener la ubicación del dispositivo, por ejemplo con FusedLocationProviderClient
        // Esto es un ejemplo simple, debes implementar el código para obtener la ubicación real

        // Obtener la ubicación (esto es solo un ejemplo)
        String location = "Lat: 19.4326, Lon: -99.1332"; // Ejemplo de ubicación en la Ciudad de México

        // Simulamos enviar la ubicación a un número de emergencia
        sendEmergencyCall("8713789035", location);
    }

    // Método para simular el envío de un mensaje con la ubicación a un número de emergencia
    private void sendEmergencyCall(String phoneNumber, String location) {
        // Emisión de una llamada (simulada) al número de emergencia
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

        // Mostrar el mensaje de la ubicación
        String message = "Estoy en emergencia, mi ubicación es: " + location;
        sendSmsToEmergencyContact(phoneNumber, message);
    }

    // Método para enviar un SMS (simulado) a un número de emergencia con la ubicación
    private void sendSmsToEmergencyContact(String phoneNumber, String message) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }
}
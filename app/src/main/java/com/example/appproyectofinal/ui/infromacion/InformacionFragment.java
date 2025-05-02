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

        binding.alertaButton.setOnClickListener(v -> {
            // Crear un Bundle para pasar los datos al siguiente fragmento
            Bundle bundle = new Bundle();
            bundle.putString("texto",
                    "Botón de Alerta: ¿Qué sucede al mantenerlo presionado?\n\n" +
                            "El botón de Alerta está diseñado para situaciones de emergencia. Si mantienes el botón presionado durante 3 segundos, la aplicación enviará una notificación urgente tanto a los contactos de emergencia que hayas registrado previamente como a los números de seguridad pública.\n\n" +

                            "Además de enviar la notificación, la app compartirá tu ubicación, lo que permitirá a tus contactos y a las autoridades localizarlos rápidamente y tomar las acciones necesarias para ayudarte.\n\n" +

                            "Este botón es una herramienta importante para garantizar tu seguridad, ya que proporciona una forma rápida y eficiente de notificar tu situación de emergencia. No dudes en usarlo en caso de que necesites asistencia inmediata.\n\n" +

                            "Recuerda que solo se activará al mantenerlo presionado por 3 segundos, por lo que es importante estar consciente de su funcionamiento y tener configurados los contactos y números de emergencia previamente.");
            bundle.putInt("imagenResId", R.drawable.botonalerta);

            // Realizar la navegación al fragmento Explicacion, pasando el Bundle
            Navigation.findNavController(v).navigate(R.id.action_informacion_to_explicacionFragment, bundle);
        });

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

        binding.modalidadButton.setOnClickListener(v -> {
            // Crear un Bundle para pasar los datos al siguiente fragmento
            Bundle bundle = new Bundle();
            bundle.putString("texto",
                    "Modalidades de violencia:\n\n" +
                            "1. Violencia familiar:\n" +
                            "Ocurre dentro del hogar y puede ejercerse por cualquier integrante de la familia, afectando principalmente a mujeres, niñas, niños y personas adultas mayores.\n\n" +

                            "2. Violencia laboral:\n" +
                            "Se manifiesta en espacios de trabajo mediante acoso, hostigamiento, amenazas o discriminación por razón de género.\n\n" +

                            "3. Violencia institucional:\n" +
                            "Es ejercida por servidores públicos que impiden, obstaculizan o restringen el acceso a servicios o derechos.\n\n" +

                            "4. Violencia comunitaria:\n" +
                            "Se da en espacios públicos, como calles, transporte o lugares de reunión, y suele manifestarse con acoso, intimidación o agresiones sexuales.\n\n" +

                            "5. Violencia digital:\n" +
                            "Se presenta mediante medios digitales, como redes sociales, mensajería o correo electrónico, e incluye amenazas, difusión de contenido íntimo, suplantación o vigilancia.\n\n" +

                            "6. Violencia docente:\n" +
                            "Sucede en instituciones educativas, cuando alguna figura de autoridad abusa de su posición para agredir o discriminar.\n\n" +

                            "7. Violencia obstétrica:\n" +
                            "Es ejercida por personal médico durante el embarazo, parto o puerperio, al ignorar, maltratar o intervenir sin consentimiento informado.");
            bundle.putInt("imagenResId", R.drawable.modalidadesv);

            // Realizar la navegación al fragmento Explicacion, pasando el Bundle
            Navigation.findNavController(v).navigate(R.id.action_informacion_to_explicacionFragment, bundle);
        });

        binding.centrosButton.setOnClickListener(v -> {
            // Crear un Bundle para pasar los datos al siguiente fragmento
            Bundle bundle = new Bundle();
            bundle.putString("texto","Instituciones y programas de apoyo:\n\n" +
                    "1. EQUIS Justicia para las Mujeres:\n" +
                    "Organización feminista que impulsa el acceso a la justicia para mujeres, promoviendo leyes, políticas públicas y sistemas judiciales con perspectiva de género.\n\n" +

                    "2. APIS Sureste Fundación para la Equidad:\n" +
                    "Organización que brinda atención integral a mujeres víctimas de violencia, promoviendo la equidad de género y la defensa de los derechos humanos en el sureste de México.\n\n" +

                    "3. CAFIS (Colectiva de Apoyo para Familias de Personas Desaparecidas):\n" +
                    "Grupo que brinda acompañamiento psicosocial y legal a familias de personas desaparecidas, con enfoque de género y derechos humanos.\n\n" +

                    "4. DAS (Defensoras de Acceso a la Salud):\n" +
                    "Colectivo que defiende el derecho de las mujeres a una salud digna, accesible y libre de violencia, particularmente en comunidades marginadas.\n\n" +

                    "5. GESMujer (Grupo de Estudios sobre la Mujer Rosario Castellanos):\n" +
                    "Organización que promueve la equidad de género y el empoderamiento de las mujeres en Oaxaca mediante educación, atención y políticas públicas.\n\n" +

                    "6. INCIDEFEME:\n" +
                    "Instituto que trabaja por el desarrollo integral de las mujeres, con enfoque en salud, economía y participación política, a través de programas comunitarios.\n\n" +

                    "7. Ixmucané:\n" +
                    "Asociación enfocada en los derechos de las mujeres indígenas, ofreciendo espacios de formación, denuncia y fortalecimiento cultural.\n\n" +

                    "8. UNASSE (Unión Nacional de Sociedades Solidarias de Economía):\n" +
                    "Organización que fomenta la economía solidaria con enfoque de género, generando redes de apoyo económico entre mujeres.\n\n" +

                    "9. Yoltika:\n" +
                    "Colectiva feminista dedicada a la sanación comunitaria, acompañamiento psicosocial y fortalecimiento del tejido social en contextos de violencia.");
            bundle.putInt("imagenResId", R.drawable.logosinstcontrv);

            // Realizar la navegación al fragmento Explicacion, pasando el Bundle
            Navigation.findNavController(v).navigate(R.id.action_informacion_to_explicacionFragment, bundle);
        });

        binding.atencionButton.setOnClickListener(v -> {
            // Crear un Bundle para pasar los datos al siguiente fragmento
            Bundle bundle = new Bundle();
            bundle.putString("texto","Instituciones de apoyo y servicios en caso de violencia:\n\n" +
                    "1. Instituto Municipal de la Mujer:\n" +
                    "Es una instancia local que promueve la igualdad de género, brinda atención psicológica, jurídica y asesoría a mujeres que enfrentan situaciones de violencia. También ofrece talleres, capacitación y acompañamiento.\n\n" +

                    "2. CJEM (Centro de Justicia para las Mujeres):\n" +
                    "Centro especializado donde se concentran servicios legales, médicos, psicológicos y sociales para mujeres víctimas de violencia. Se busca que puedan recibir atención integral en un solo lugar y sin revictimización.\n\n" +

                    "3. Seguridad Pública:\n" +
                    "Institución encargada de prevenir y atender situaciones de riesgo. A través de la policía municipal o estatal se puede solicitar auxilio inmediato, protección o canalización a otras instancias. Algunas tienen unidades especiales para atención a mujeres.\n\n" +

                    "4. Número de emergencias (911):\n" +
                    "Línea nacional gratuita disponible las 24 horas para reportar cualquier situación de violencia o peligro. Al marcar, se canaliza la atención de acuerdo con el tipo de emergencia: médica, policial o de protección civil.\n\n" +

                    "5. MUSAS Refugio para Mujeres:\n" +
                    "Espacio seguro que brinda alojamiento temporal a mujeres en situación de violencia extrema, junto con sus hijas e hijos. Ofrecen acompañamiento psicológico, legal y social para reconstruir sus vidas lejos del agresor.");
            bundle.putInt("imagenResId", R.drawable.numeros);

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

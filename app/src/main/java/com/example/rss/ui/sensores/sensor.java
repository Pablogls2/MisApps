package com.example.rss.ui.sensores;

import android.Manifest;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rss.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

//necesita implementar el listener de los sensores
public class sensor extends Fragment implements SensorEventListener {

    //atributos
    private View root;
    private TextView tvSensorOrientacion, tvSensorAce, tvSensorGrave, tvSensorMagne, tvSensorGiro, tvSensorProxi;
    private ImageView ibSensorBoton;
    private static Camera camera;
    private ImageView ivSensorImagenProxi;
    private boolean encendido = false;


    public sensor() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Se inicializan los componentes
        root = inflater.inflate(R.layout.fragment_sensor, container, false);


        tvSensorOrientacion = (TextView) root.findViewById(R.id.tvSensorOrientacion);
        tvSensorMagne = (TextView) root.findViewById(R.id.tvSensorMagne);
        tvSensorAce = (TextView) root.findViewById(R.id.tvSensorAcelerar);
        tvSensorGrave = (TextView) root.findViewById(R.id.tvSensorGrav);
        tvSensorGiro = (TextView) root.findViewById(R.id.tvSensorGiroscopio);
        tvSensorProxi = (TextView) root.findViewById(R.id.tvSensorProximidad);
        ivSensorImagenProxi = (ImageView) root.findViewById(R.id.ivSensorImagenProxi);
        ibSensorBoton = (ImageView) root.findViewById(R.id.boton);

        //se piden permisos
        pedirMultiplesPermisos2();

        //al darle al boton se activara  o desactivara la linterna
        ibSensorBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //si estaba apagada tendra el icono de play y si no el de pararla
                    if (encendido) {
                        encendido = false;
                        ibSensorBoton.setBackgroundResource(R.drawable.ic_play);
                    } else {
                        encendido = true;
                        ibSensorBoton.setBackgroundResource(R.drawable.ic_pause);
                    }

                    //para encender o apagar el flash , se coge el primer elemento del array porque la camara del flash suele ser la primera
                    CameraManager camManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = camManager.getCameraIdList()[0];
                    //dependiendo de si estaba apagada o encendida , la encerdera o apagara

                       camManager.setTorchMode(cameraId, encendido);


                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getContext(),"No tienes flash",Toast.LENGTH_LONG).show();
                }


            }
        });

        //se inicia el Manager del sensor
        SensorManager sensorManager = (SensorManager)
                getActivity().getSystemService(SENSOR_SERVICE);
        //se guarda en una lista todos los tipos de sensores
        List<Sensor> listaSensores = sensorManager.
                getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : listaSensores) {

        }
        //Comenzamos consultando si disponemos de los diferentes sensores
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

        if (!listaSensores.isEmpty()) {

            Sensor orientationSensor = listaSensores.get(0);

            sensorManager.registerListener(this, orientationSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }


        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (!listaSensores.isEmpty()) {

            Sensor acelerometerSensor = listaSensores.get(0);

            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (!listaSensores.isEmpty()) {

            Sensor magneticSensor = listaSensores.get(0);

            sensorManager.registerListener(this, magneticSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);

        if (!listaSensores.isEmpty()) {

            Sensor temperatureSensor = listaSensores.get(0);

            sensorManager.registerListener(this, temperatureSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);

        if (!listaSensores.isEmpty()) {

            Sensor temperatureSensor = listaSensores.get(0);

            sensorManager.registerListener(this, temperatureSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (!listaSensores.isEmpty()) {

            Sensor temperatureSensor = listaSensores.get(0);

            sensorManager.registerListener(this, temperatureSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }


        return root;
    }


    //se piden permisos
    private void pedirMultiplesPermisos2() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            // Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }

                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        //Toast.makeText(this, "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //evento al cambiar el valor del sensor
    public void onSensorChanged(SensorEvent evento) {

        //dependiendo del sensor cambiara su TextView correspondiente al cambiar de valor
        synchronized (this) {
            switch (evento.sensor.getType()) {

                case Sensor.TYPE_ORIENTATION:


                    tvSensorOrientacion.setText(" " + evento.values[0]);


                    break;

                case Sensor.TYPE_ACCELEROMETER:

                    for (int i = 0; i < 3; i++) {

                        tvSensorAce.setText("" + evento.values[i]);
                    }

                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:


                    tvSensorMagne.setText("" + evento.values[0]);

                    break;

                case Sensor.TYPE_GRAVITY:

                    for (int i = 0; i < 3; i++) {

                        tvSensorGrave.setText("" + evento.values[i]);
                    }


                    break;

                case Sensor.TYPE_GYROSCOPE:


                    tvSensorGiro.setText("" + evento.values[0]);


                    break;
                case Sensor.TYPE_PROXIMITY:


                    tvSensorProxi.setText("" + evento.values[0]);
                    if (evento.values[0] < 5) {
                        ivSensorImagenProxi.setVisibility(View.VISIBLE);
                    } else {
                        ivSensorImagenProxi.setVisibility(View.INVISIBLE);
                    }

                    break;

            }

        }
    }

}

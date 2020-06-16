package com.example.rss.ui.mapas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rss.AdaptadorRecycled;
import com.example.rss.Noticia;
import com.example.rss.R;
import com.example.rss.ui.detalles.DetallesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class mapa extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private View root;
    private GoogleMap mMap;

    private Bundle mBundle;


    private static final int LOCATION_REQUEST_CODE = 1; // Para los permisos
    private boolean permisos = false;

    // Para obtener el punto actual (no es necesario para el mapa)
    // Pero si para obtener las latitud y la longitud
    private FusedLocationProviderClient mPosicion;

    private Location miUltimaLocalizacion;
    private LatLng posDefecto = new LatLng(38.6901212, -4.1086075);
    private LatLng posActual = posDefecto;

    // Marcador actual
    private Marker marcadorActual = null;

    // Marcador marcadorTouch
    private Marker marcadorTouch = null;
    private Marker marcadorInicio;
    private Marker marcadorFinal;

    // Posición actual con eventos y no hilos
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private Button btn_start;
    private Button btn_stop;
    private TextView tvMapaMetros;
    private Button btnMapaGuardar;

    private ArrayList<LatLng> recorrido;

    private Timer timer = null;


    private Chronometer chronometer;

    private String nombre_fichero_xml = null;
    private String ruta_path = null;
    private boolean cargar = false;
    private ArrayList<Punto> puntos;

    private Marker avance = null;

    public mapa() {

    }

    //constructor para cuando se cargue una ruta
    public mapa(boolean c) {
        this.cargar = c;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_mapa, container, false);

        //se inicializan los componentes
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());


        btn_start = (Button) root.findViewById(R.id.btnMapaStart);
        btn_stop = (Button) root.findViewById(R.id.btnMapaFinish);
        tvMapaMetros = (TextView) root.findViewById(R.id.tvMapaMetros);
        btnMapaGuardar = (Button) root.findViewById(R.id.btnMapaGuardar);
        puntos = new ArrayList<Punto>();
        chronometer = root.findViewById(R.id.chronometer);


        //se oculta al principio el boton de guardar y los metros
        btnMapaGuardar.setVisibility(View.INVISIBLE);

        tvMapaMetros.setVisibility(View.INVISIBLE);


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_stop.setEnabled(false);
        btnMapaGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //llamamos al metodo para guardar la ruta
                guardarRuta();
            }
        });


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se vuelven a mostrar los componentes necesarios
                btnMapaGuardar.setVisibility(View.INVISIBLE);
                tvMapaMetros.setVisibility(View.INVISIBLE);

                //se reinicia el mapa y el cronometro
                mMap.clear();
                chronometer.setBase(SystemClock.elapsedRealtime());

                chronometer.start();

                Toast.makeText(getContext(),

                        " Dale Duro!!",
                        Toast.LENGTH_SHORT).show();

                //se marca el comienzo de la ruta con un marcador verde
                marcadorInicio = mMap.addMarker(new MarkerOptions()
                                // Posición
                                .position(posActual)
                                // Título
                                .title("¡INICIO!")
                                // Subtitulo

                                // Color o tipo d icono
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ayuntamiento))
                );
                //empieza el actualizador que ira siguiendo cada 5 segundos al usuario
                autoActualizador(false);
                //se permite parar la ruta
                btn_stop.setEnabled(true);


            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mostramos el boton de guardar
                btnMapaGuardar.setVisibility(View.VISIBLE);
                chronometer.stop();
                Toast.makeText(getContext(),

                        " Buen Trabajo!!",
                        Toast.LENGTH_SHORT).show();
                //se marca la posicion de final de ruta
                marcadorFinal = mMap.addMarker(new MarkerOptions()
                                // Posición
                                .position(posActual)
                                // Título
                                .title("¡FIN!")
                                // Subtitulo

                                // Color o tipo d icono
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ayuntamiento))
                );
                if (timer != null) {
                    //se apaga la ruta
                    timer.cancel();


                    tvMapaMetros.setVisibility(View.VISIBLE);

                    //se guarda en un array float el trayecto en metros
                    float[] results = new float[1];
                    Location.distanceBetween(marcadorInicio.getPosition().latitude, marcadorInicio.getPosition().longitude,
                            marcadorFinal.getPosition().latitude, marcadorFinal.getPosition().longitude,
                            results);

                    float metros = results[0];

                    //se muestra con formato legible
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String metrosFormateado = decimalFormat.format(metros);


                    tvMapaMetros.setText(metrosFormateado);
                    tvMapaMetros.append(" metros");
                }


            }
        });


        ////
        //si se ha cargado una ruta se  recoge en un bundle el path de la ruta y ejecuta la tarea asincrona
        if (cargar) {
            Bundle b = this.getArguments();
            ruta_path = b.getString("ruta");
            new AsynRuta().execute();

        }


        return root;


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        setHasOptionsMenu(true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /// Solicitamos prmisos de Localización
        solicitarPermisos();


        // Configurar IU Mapa
        configurarIUMapa();


        // activa el evento de marcadores Touc
        activarEventosMarcdores();

        // Obtenemos la posición GPS

        obtenerPosicion();

        // Situar la camara inicialmente a una posición determinada
        situarCamaraMapa();


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Crear el LocationRequest

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 segundos en milisegundos
                .setFastestInterval(1 * 1000); // 1 segundo en milisegundos


    }

    /**
     * Metodo para guardar la ruta del usuario
     */
    private void guardarRuta() {
        final String[] nombre = {null};

        //mostramos un alerdialog para pedir al usuario el nombre de la ruta
        final AlertDialog alertDialogBuilder = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater li = this.getActivity().getLayoutInflater();

        View dialog = li.inflate(R.layout.dialogo_guardar_ruta, null);
        Button btnMapaOk = (Button) dialog.findViewById(R.id.btnDialogoOk);
        Button btnMapaCancel = (Button) dialog.findViewById(R.id.btnDialogoCancel);


        final EditText userInput = (EditText) dialog.findViewById(R.id.etUserInput);

        //si pulsa cancelar se cierra el dialogo y no hace nada mas
        btnMapaCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder.dismiss();


            }
        });
        //si pulsa ok
        btnMapaOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cogemos lo que el usuario ha introducido y cerramos el dialogo
                nombre[0] = userInput.getText().toString();
                alertDialogBuilder.dismiss();
                nombre_fichero_xml = nombre[0];
                Log.e("da", "d" + nombre_fichero_xml);

                /////
                try {
                    //se comprueba que ha introducido algo
                    Log.e("a", "d" + nombre_fichero_xml);
                    if (!nombre_fichero_xml.isEmpty()) {

                        //cogemos el fichero del almacenamiento publico
                        File root = Environment
                                .getExternalStorageDirectory();
                        //si se puede escribir en el creamos un fichero nuevo con el nombre del usuario y acabando en .xml
                        if (root.canWrite()) {
                            File gpxfile = new File(root, nombre_fichero_xml
                                    + ".xml");
                            FileWriter gpxwriter = new FileWriter(
                                    gpxfile);
                            BufferedWriter out = new BufferedWriter(
                                    gpxwriter);
                            //empezamos a escribir el xml con la ruta con formato
                            /*
                            * <Ruta>
                            *   <punto>
                            *       <latitud></latitud>
                            *       <longitud></longitud>
                            *   </punto>
                            * </Ruta>
                            *
                            * */
                            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                    + "\n"

                                    + "\n" + "<Ruta>" + "\n");
                            for (int i = 0; i < recorrido.size(); i++) {
                                out.write("<punto>" + "\n"
                                        + "<latitud>"
                                        + recorrido.get(i).latitude
                                        + "</latitud>"
                                        + "\n"
                                        + "<longitud>"
                                        + recorrido.get(i).longitude
                                        + "</longitud>"
                                        + "\n"
                                        + "</punto>"
                                        + "\n"

                                );
                            }
                            out.write("</Ruta>");
                            out.close();
                            Toast toast1 =
                                    Toast.makeText(getContext(),
                                            "RUTA GUARDADA", Toast.LENGTH_SHORT);

                            toast1.show();

                        }
                    } else {
                        Toast toast1 =
                                Toast.makeText(getContext(),
                                        "RUTA NO GUARDADA", Toast.LENGTH_SHORT);
                        toast1.show();
                    }


                } catch (IOException e) {
                    Log.e("Cant write", "Could not write file "
                            + e.getMessage());
                }

                /////


            }
        });

        // se crea el dialogo
        alertDialogBuilder.setView(dialog);

        // se muestra
        alertDialogBuilder.show();
        /////
        Log.e("d", "d" + nombre_fichero_xml);


    }

    /**
     * Metodo para comprobar que  el gps este  encendido, en proximas versionas
     *
     * @return
     */
    private boolean comprobarGPS() {
        boolean comprobacion = false;


        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(getContext())
                    .setMessage("ACTIVA LA UBICACION").setNegativeButton("NO", null)
                    .setPositiveButton("ACTIVA LA UBICACION", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })


                    .show();
            return true;
        } else {
            comprobacion = true;
        }


        return comprobacion;
    }

    private void activarEventosMarcdores() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Creamos el marcador
                // Borramos el marcador Touch si está puesto
                if (marcadorTouch != null) {
                    marcadorTouch.remove();
                }
                marcadorTouch = mMap.addMarker(new MarkerOptions()
                        // Posición
                        .position(point)
                        // Título
                        .title("Marcador Touch")
                        // Subtitulo
                        .snippet("El Que tú has puesto")
                        // Color o tipo d icono
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

            }
        });


    }

    private void situarCamaraMapa() {
        // movemos la camara al usuario
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posActual));
    }

    private void configurarIUMapa() {
        // activamos los eventos de marcador
        mMap.setOnMarkerClickListener(this);

        // Activar Boton de Posición actual
        if (permisos) {
            // Si tenemos permisos pintamos el botón de la localización actual
            mMap.setMyLocationEnabled(true);
        }

        //ponemos el tipo de mapa en mi caso hibrido
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        UiSettings uiSettings = mMap.getUiSettings();
        // Activamos los gestos
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        // Activamos la brújula
        uiSettings.setCompassEnabled(true);
        // Activamos los controles de zoom
        uiSettings.setZoomControlsEnabled(true);
        // Activamos la brújula
        uiSettings.setCompassEnabled(true);
        // Actiovamos la barra de herramientas
        uiSettings.setMapToolbarEnabled(true);

        // Hacemos el zoom por defecto mínimo
        mMap.setMinZoomPreference(13.0f);
        // Señalamos el tráfico
        mMap.setTrafficEnabled(true);
    }


    // Evento de procesar o hacer click en un marker
    @Override
    public boolean onMarkerClick(Marker marker) {
        //al pulsar en tu marcador se mostrara ese toast
        String titulo = marker.getTitle();
        switch (titulo) {

            case "Marcador Touch":
                Toast.makeText(getContext(), "Estás en: " + marker.getPosition().latitude + "," + marker.getPosition().longitude,
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return false;
    }

    // Obtenermos y leemos directamente el GPS
    private void obtenerPosicion() {
        try {
            if (permisos) {
                // Lo lanzamos como tarea concurrente
                Task<Location> local = mPosicion.getLastLocation();
                local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Actualizamos la última posición conocida
                            miUltimaLocalizacion = task.getResult();
                            posActual = new LatLng(miUltimaLocalizacion.getLatitude(),
                                    miUltimaLocalizacion.getLongitude());

                        } else {
                            Log.d("GPS", "No se encuetra la última posición.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        } catch (Exception e) {

        }
    }

    // Para dibujar el marcador actual
    private void marcadorPosicionActual() {



        // Borramos el arcador actual si está puesto
        if (marcadorActual != null) {
            marcadorActual.remove();
        }
        // añadimos el marcador actual en violeta
        marcadorActual = mMap.addMarker(new MarkerOptions()
                // Posición
                .position(posActual)
                // Título
                .title("Mi Localización")
                // Subtitulo
                .snippet("Localización actual")
                // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        );

    }


    // solicitamos los permisos de ubicacion
    public void solicitarPermisos() {

        // Si tenemos los permisos
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Activamos el botón de lalocalización
            permisos = true;
        } else {
            // Si no
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }

    // Para los permisoso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permisos = false;
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisos = true;
            } else {
                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
            }
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisos = true;
            } else {
                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }

    // Metodo con un hilo para ir cogiendo la ubicacion del usuario
    private void autoActualizador(boolean bandera) {
        final Handler handler = new Handler();
        final boolean bander = bandera;
        //con este array vamos guardando los marcadores que se han usado para mas adelante usarlos
        recorrido = new ArrayList<>();
        timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Obtenemos la posición
                            obtenerPosicion();


                            //ponemos un marcador
                            avance = mMap.addMarker(new MarkerOptions()
                                    // Posición
                                    .position(posActual)
                                    // Título
                                    .title("punto")
                                    // Subtitulo
                                    .snippet("punto")
                                    // Color o tipo d icono
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            //guardamos la posicion del marcador en el array
                            LatLng avance_metros = avance.getPosition();
                            recorrido.add(avance_metros);


                            //cuando sea true se parara el timer
                            if (bander) {
                                timer.cancel();
                                timer = null;
                            }

                        } catch (Exception e) {
                            Log.e("TIMER", "Error: " + e.getMessage());
                        }
                    }
                });
            }


        };
        // Actualizamos cada 5 segundos
        timer.schedule(doAsyncTask, 0, 5000);
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d("Mapa", location.toString());
        miUltimaLocalizacion = location;
        posActual = new LatLng(miUltimaLocalizacion.getLatitude(),
                miUltimaLocalizacion.getLongitude());
        // Añadimos un marcador especial para poder operar con esto
        marcadorPosicionActual();

        situarCamaraMapa();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {

                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {

            Log.i("Mapa", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cargar_ruta, menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_cargar:
                //cuando pulse el icono del + ira al fragment con la rutas guardadas
                verRutas de = new verRutas();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, de);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


                break;


        }

        return super.onOptionsItemSelected(item);
    }

    //tarea asincrona para leer el xml con la ruta elegida por el usuario al cargar
    class AsynRuta extends AsyncTask<Void, Void, ArrayList<Punto>> {

        Double latitud;
        Double longitud;


        public ArrayList<Punto> doInBackground(Void... params) {

            //se coge la ruta usando el path recogido del bundle que se lleno en verRutas
            File ruta = new File(ruta_path);
            Log.e("puta ruta", "d" + ruta.getPath());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                //accede mediente el File para leer el xml
                Document document = builder.parse(ruta);
                //mira punto por punto y recorre los nodos
                NodeList items = document.getElementsByTagName("punto");

                for (int i = 0; i < items.getLength(); i++) {
                    Node nodo = items.item(i);
                    for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {
                        if (n.getNodeName().equals("latitud")) {

                            latitud = Double.parseDouble(n.getTextContent());
                            Log.e("nodo", " a" + latitud);
                        }


                        if (n.getNodeName().equals("longitud")) {
                            longitud = Double.parseDouble(n.getTextContent());
                        }


                    }
                    //crea un Punto con la latitud y longitud y la guarda en un array de puntos
                    Punto p = new Punto(latitud, longitud);
                    puntos.add(p);


                }

            } catch (ParserConfigurationException e) {
                System.err.println("No se pudo crear una instancia de DocumentBuilder");
            } catch (SAXException e) {
                System.err.println("Error SAX al parsear el archivo");
            } catch (IOException e) {
                System.err.println("Se produjo un error de E/S");
            } catch (DOMException e) {
                System.err.println("Se produjo un error del DOM");

            }


            //
            return puntos;
        }


        protected void onPostExecute(ArrayList<Punto> lista) {

            //recorre el array de puntos y los pinta en el mapa
            for (int i = 0; i < puntos.size(); i++) {
                LatLng pos = new LatLng(lista.get(i).getLatitud(), lista.get(i).getLongitud());
                Log.e("punto", "s" + pos.toString());


                if (i == 0) {
                    mMap.addMarker(new MarkerOptions()
                            // Posición
                            .position(pos)
                            // Título
                            .title("INICIO")
                            // Subtitulo
                            .snippet("bien hecho")
                            // Color o tipo d icono
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    );
                } else {
                    if (i == puntos.size() - 1) {
                        mMap.addMarker(new MarkerOptions()
                                // Posición
                                .position(pos)
                                // Título
                                .title("FINAL")
                                // Subtitulo
                                .snippet("bien hecho")
                                // Color o tipo d icono
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        );
                    } else {
                        mMap.addMarker(new MarkerOptions()
                                // Posición
                                .position(pos)
                                // Título
                                .title("punto")
                                // Subtitulo
                                .snippet("bien hecho")
                                // Color o tipo d icono
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                }


            }


        }


    }

}

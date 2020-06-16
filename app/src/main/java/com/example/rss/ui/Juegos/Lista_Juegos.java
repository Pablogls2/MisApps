package com.example.rss.ui.Juegos;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.rss.R;

import java.util.ArrayList;
import java.util.List;

import com.example.rss.BDControlador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

public class Lista_Juegos extends Fragment {


    private ArrayList<Juego> listaJuegos;

    private View root;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Paint paint = new Paint();
    ConstraintLayout constr;
    private AdaptadorListaJuegos adapter;
    private FloatingActionButton fabBotonFlotante;
    private Spinner spseleccion;
    String seleccion;


    public static Lista_Juegos newInstance() {
        return new Lista_Juegos();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.lista_juegos_fragment, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerListaJuegos);
        constr = (ConstraintLayout) root.findViewById(R.id.constraint);
        listaJuegos = new ArrayList<Juego>();
        this.fabBotonFlotante = (FloatingActionButton) root.findViewById(R.id.fabListaJuegosBoton);
        this.spseleccion = (Spinner) root.findViewById(R.id.spSeleccionOrden);
        //parte  del spinner para ordenar
        String[] plataformas = {" ", "Por nombre Ascendente", "Por nombre Descendente", "Por plataforma Ascendente", "Por plataforma Descendente", "Por fecha Ascendente", "Por fecha Descendente", "Por precio Ascendente", "Por precio Descendente"};
        spseleccion.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, plataformas));

        //evento para saber que ordenacion se quiere usar
        spseleccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //se hace un switch dependiendo de lo seleccionado
                seleccion = (String) spseleccion.getSelectedItem();
                switch (seleccion) {

                    case "Por nombre Ascendente":
                        // si es ascendente se llama a seleccionAsc con el campo correspondiente y se recarga el recycler
                        seleccionAsc("titulo");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por nombre Descendente":
                        seleccionDesc("titulo");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por plataforma Ascendente":
                        seleccionAsc("plataforma");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por plataforma Descendente":
                        seleccionDesc("plataforma");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por fecha Ascendente":
                        seleccionAsc("fecha");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por fecha Descendente":
                        seleccionDesc("fecha");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por precio Ascendente":
                        seleccionAsc("precio");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;
                    case "Por precio Descendente":
                        seleccionDesc("precio");
                        adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        // se presenta en formato lineal
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //se piden los permisos necesarios
        pedirMultiplesPermisos();


        class AsynJuegos extends AsyncTask<Void, Integer, Integer> {


            protected Integer doInBackground(Void... params) {
                int r = 0;
                //se cargan los datos de la bbdd
                cargarDatos();


                return r;

            }


            protected void onPostExecute(Integer re) {
                Log.e("Funciona", "eeeeeee");


                //se carga en el adapadator la lista rellena de juegos y se le pasa el contexto del fragment
                adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
                recyclerView.setHasFixedSize(true);
                // se presenta en formato lineal
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                //se le aplica el adaptador al recyclerView
                recyclerView.setAdapter(adapter);


            }


        }
        //se ejecuta la tarea asincrona
        new AsynJuegos().execute();

        //se inicia el swipe horizontal
        iniciarSwipeHorizontal();
        //evento del boton flotante para cargar el fragment de crear un juego
        fabBotonFlotante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DetallesJuego_fragment de = DetallesJuego_fragment.newInstance("crear");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, de);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });
        //se inicia el swipe para recargar
        iniciarSwipeRecargar();


        return root;
    }

    /**
     * Metodo que dependiendo del campo hace una consulta ordenada ascendente  por un campo u otro
     *
     * @param orden
     */
    private void seleccionAsc(String orden) {
        //se limpia la lista
        listaJuegos.clear();
        //Abrimos la base de datos en modo lectura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getReadableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos ordenados por el campo dado
            Cursor c = bd.rawQuery(" SELECT id ,titulo , plataforma , fecha  , precio  , imagen  FROM Juegos ORDER BY " + orden + " ASC", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros y se llena la lista de juegos
                do {
                    try {

                        int id = c.getInt(0);
                        String titulo = c.getString(1);
                        String plataforma = c.getString(2);
                        String fecha = c.getString(3);
                        Float precio = c.getFloat(4);
                        // String img = c.getBlob(4);
                        byte[] img = c.getBlob(5);
                        String i = new String(img);
                        Bitmap imagen = base64ToBitmap(i);
                        Juego m = new Juego(titulo, plataforma, fecha, precio, imagen, id);
                        Log.e("select ", "e" + m.getTitulo());
                        listaJuegos.add(m);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "¡PROBLEMA AL CARGAR!", Toast.LENGTH_LONG).show();
                    }
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            bd.close();
        }
    }

    /**
     * Metodo que dependiendo del campo hace una consulta ordenada descendente por un campo u otro
     *
     * @param orden
     */
    private void seleccionDesc(String orden) {
        // se limpia la lista
        listaJuegos.clear();
        //Abrimos la base de datos  en modo lectura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getReadableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos ordenados por el campo dado de forma descenciente
            Cursor c = bd.rawQuery(" SELECT id ,titulo , plataforma , fecha  , precio  , imagen  FROM Juegos ORDER BY " + orden + " DESC", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros y se llena la lista con los juegos de la bbdd
                do {
                    try {

                        int id = c.getInt(0);
                        String titulo = c.getString(1);
                        String plataforma = c.getString(2);
                        String fecha = c.getString(3);
                        Float precio = c.getFloat(4);
                        // String img = c.getBlob(4);
                        byte[] img = c.getBlob(5);
                        String i = new String(img);
                        Bitmap imagen = base64ToBitmap(i);
                        //Bitmap imagen=base64ToBitmap(c.getBlob(4));
                        Juego m = new Juego(titulo, plataforma, fecha, precio, imagen, id);
                        Log.e("select ", "e" + m.getTitulo());
                        listaJuegos.add(m);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "¡PROBLEMA AL CARGAR!", Toast.LENGTH_LONG).show();
                    }
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            bd.close();
        }
    }

    /**
     * Metodo para iniciar el swipe de recargar
     */
    private void iniciarSwipeRecargar() {
        // Para refrescar y volver al cargar

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // se asignan los colores
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.fondoProgress);
                // Volvemos a cargar los datos
                new AsynJuegos2().execute();


            }
        });
    }

    /**
     * Tarea asincrona para recargar con un progressBar
     */
    class AsynJuegos2 extends AsyncTask<Void, Integer, Integer> {
        private ProgressBar progressBar;

        protected void onPreExecute() {
            // Saco la barra de progreso
            progressBar = root.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }


        protected Integer doInBackground(Void... params) {
            int r = 0;
            //se cargan los datos
            listaJuegos = new ArrayList<>();
            cargarDatos();


            return r;

        }


        protected void onPostExecute(Integer re) {
            Log.e("Funciona", "eeeeeee");

            progressBar.setVisibility(View.GONE);
            //se carga en el adapadator la lista rellena de juegos y se le pasa el contexto del fragment
            adapter = new AdaptadorListaJuegos(listaJuegos, getContext(), getActivity(), getFragmentManager());
            recyclerView.setHasFixedSize(true);
            // se presenta en formato lineal
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            //se le aplica el adaptador al recyclerView
            recyclerView.setAdapter(adapter);

            swipeRefreshLayout.setRefreshing(false);

        }


    }

    /**
     * Metodo para consultar los datos
     */
    private void cargarDatos() {

        //se llama al metodo de consulta
        seleccionarData();


    }

    /**
     * Metodo que llama al metodo para borrar datos del adaptador del recyclerView
     *
     * @param posicion
     */
    private void borrarDatos(int posicion) {

        adapter.borrarDatos(posicion);


    }

    /**
     * Metodo que llama al metodo para editar datos del adaptador del recyclerView
     *
     * @param posicion
     */
    public void editarDatos(int posicion) {
        adapter.editarJuegos(posicion);
    }

    /**
     * Metodo que hace una consulta de todos los registros de la bbbdd
     */
    private void seleccionarData() {
        listaJuegos.clear();
        //Abrimos la base de datos  en modo lectura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getReadableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todos
            Cursor c = bd.rawQuery(" SELECT id ,titulo , plataforma , fecha  , precio  , imagen  FROM Juegos", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros y se llena la lista de juegos
                do {
                    try {

                        int id = c.getInt(0);
                        String titulo = c.getString(1);
                        String plataforma = c.getString(2);
                        String fecha = c.getString(3);
                        Float precio = c.getFloat(4);
                        // String img = c.getBlob(4);
                        byte[] img = c.getBlob(5);
                        String i = new String(img);
                        Bitmap imagen = base64ToBitmap(i);
                        //Bitmap imagen=base64ToBitmap(c.getBlob(4));
                        Juego m = new Juego(titulo, plataforma, fecha, precio, imagen, id);
                        Log.e("select ", "e" + m.getTitulo());
                        listaJuegos.add(m);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "¡PROBLEMA AL CARGAR!", Toast.LENGTH_LONG).show();
                    }
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            bd.close();
        }
    }

    /**
     * Metodo par el swipe horizontal
     */
    private void iniciarSwipeHorizontal() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            // Evento al mover
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                // Si nos movemos a la izquierda
                if (direction == ItemTouchHelper.LEFT) {
                    borrarDatos(position);


                    // Si es a la derecha
                } else {
                    editarDatos(position);
                }
            }

            // Con este codigo se dibujarian los iconos y efectos al deslizar pero da fallo, en algunos terminales funciona y en otros no
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // Si es dirección a la derecha: izquierda->derecta
                    if (dX > 0) {

                        paint.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_editar);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);

                        // Caso contrario
                    } else {

                        paint.setColor(Color.rgb(153, 0, 0));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Metodo para pedir los permisos necesarios
     */
    private void pedirMultiplesPermisos() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this.getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                        Toast.makeText(getContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Metodo que recibe un string en base 64 y devuelve un Bitmap
     *
     * @param b64
     * @return
     */
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


}

package com.example.rss.ui.Juegos;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rss.R;
import com.example.rss.BDControlador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class DetallesJuego_fragment extends Fragment {

    private DetallesJuegoFragmentViewModel mViewModel;
    private View root;
    private String t;
    private EditText etDetallesTitulo;
    private EditText etDetallesPrecio;
    private EditText etDetallesFecha;
    private Spinner sDetallesPlataforma;
    private Button btnDetallesJuegoImagen;
    private Button btnDetallesJuegoAdd;
    private ImageView ivDetallesJuegoImg;
    private Uri photoURI;
    private static final String IMAGE_DIRECTORY = "/misApps";
    private Bitmap imagenFinal;

    private Juego ju;

    private String imagen;

    private static final int GALERIA = 1;
    private static final int CAMARA = 2;


    public static DetallesJuego_fragment newInstance(String tipo) {

        Bundle b = new Bundle();
        b.putString("tipo", tipo);

        DetallesJuego_fragment f = new DetallesJuego_fragment();
        f.setArguments(b);


        return f;
    }

    public static DetallesJuego_fragment newInstance(String tipo, Juego j) {

        Bundle b = new Bundle();
        b.putString("tipo", tipo);
        b.putSerializable("Juego", j);

        DetallesJuego_fragment f = new DetallesJuego_fragment();
        f.setArguments(b);


        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.detalles_juego_fragment, container, false);

        Bundle b = getArguments();
        etDetallesFecha = (EditText) root.findViewById(R.id.etDetalleJuegoFecha);
        etDetallesTitulo = (EditText) root.findViewById(R.id.etDetalleJuegoTitulo);
        etDetallesPrecio = (EditText) root.findViewById(R.id.etDetalleJuegoPrecio);
        sDetallesPlataforma = (Spinner) root.findViewById(R.id.sDetallePlataforma);
        btnDetallesJuegoImagen = (Button) root.findViewById(R.id.btnDetallesJuegosImagen);
        ivDetallesJuegoImg = (ImageView) root.findViewById(R.id.ivDetallesJuegoImg);
        btnDetallesJuegoAdd = (Button) root.findViewById(R.id.btnDetalleJuegoAdd);


        //para que el teclado no se vuelva loco
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        //dependendiendo del tipo de visualizacion los componentes tendran un comportamiento diferente
        //modo para crear:
        if (b.getString("tipo").equals("crear")) {

            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Creación", Toast.LENGTH_SHORT);
            toast1.show();
            // se rellena el spinner con las plataformas disponibles
            String[] plataformas = {"Nintendo Switch", "Sony PS4", "MS XBOX360", "PC", "Digital"};
            sDetallesPlataforma.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, plataformas));


            //evento para añadir una imagen para el juego
            btnDetallesJuegoImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mostrarDialogoFoto();
                }
            });
            //evento del boton añadir
            btnDetallesJuegoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //se comprueban los campos para que esten rellenos
                    if (etDetallesTitulo.getText().toString().equals("") || etDetallesFecha.getText().toString().equals("") || etDetallesPrecio.getText().toString().equals("") || imagenFinal == null || etDetallesPrecio.getText().toString().equals(".")) {

                        Toast.makeText(getActivity(), "¡Compruebe los campos por favor!", Toast.LENGTH_LONG).show();
                    } else {
                        Juego j = new Juego(etDetallesTitulo.getText().toString(), sDetallesPlataforma.getSelectedItem().toString(), etDetallesFecha.getText().toString(), Float.parseFloat(etDetallesPrecio.getText().toString()), imagenFinal);
                        //se comprueba que no este repetido en la misma plataforma
                        if (!comprobarRepetidos(etDetallesTitulo.getText().toString(), sDetallesPlataforma.getSelectedItem().toString())) {
                            Toast.makeText(getActivity(), "¡Juego Repetido en la misma plataforma!", Toast.LENGTH_SHORT).show();
                        } else {
                            // se inserta
                            insertarData(j);
                            Toast.makeText(getActivity(), "¡Juego Guardado!", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
            });

            //evento para elegir la fecha del juego
            etDetallesFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.etDetalleJuegoFecha:
                            showDatePickerDialog();
                            break;
                    }
                }
            });


        }

        //modo para ver:
        if (b.getString("tipo").equals("ver")) {
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Visualización", Toast.LENGTH_SHORT);
            toast1.show();
            //no hacen falta botones por lo que se hacen invisibles
            btnDetallesJuegoAdd.setVisibility(View.INVISIBLE);
            btnDetallesJuegoImagen.setVisibility(View.INVISIBLE);

            // se recoge del bundle dado el juego seleccionado y se rellenan los campos del layout
            Juego j = (Juego) b.getSerializable("Juego");
            etDetallesTitulo.setText(j.getTitulo());
            etDetallesFecha.setText(j.getFecha());
            etDetallesPrecio.setText(j.getPrecio().toString());
            String[] plata = new String[1];
            plata[0] = j.getPlatamforma();
            sDetallesPlataforma.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, plata));
            ivDetallesJuegoImg.setImageBitmap(j.getImagen());

            // no se permite modificar los campos del layout
            etDetallesPrecio.setFocusable(false);
            etDetallesFecha.setFocusable(false);
            etDetallesTitulo.setFocusable(false);
            sDetallesPlataforma.setFocusable(false);


        }

        //modo para borrar
        if (b.getString("tipo").equals("borrar")) {
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Borrar ", Toast.LENGTH_SHORT);
            toast1.show();
            // se cambia el texto del boton de añadir por borrar
            btnDetallesJuegoAdd.setText("borrar");
            btnDetallesJuegoImagen.setVisibility(View.INVISIBLE);

            //se recoge el juego del bundle dado y se muestran sus datos
            ju = (Juego) b.getSerializable("Juego");
            etDetallesTitulo.setText(ju.getTitulo());
            etDetallesFecha.setText(ju.getFecha());
            etDetallesPrecio.setText(ju.getPrecio().toString());
            String[] plata = new String[1];
            plata[0] = ju.getPlatamforma();
            sDetallesPlataforma.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, plata));
            ivDetallesJuegoImg.setImageBitmap(ju.getImagen());

            //no se permite modificar campos
            etDetallesPrecio.setFocusable(false);
            etDetallesFecha.setFocusable(false);
            etDetallesTitulo.setFocusable(false);
            sDetallesPlataforma.setFocusable(false);

            //evento del boton de borrar
            btnDetallesJuegoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mostrarDialogoBorrar(ju.getId());

                }
            });


        }

        //modo par editar
        if (b.getString("tipo").equals("editar")) {
            //se recoge el juego del bundle
            ju = (Juego) b.getSerializable("Juego");
            //se rellenan los campos del layout
            etDetallesTitulo.setText(ju.getTitulo());
            etDetallesFecha.setText(ju.getFecha());
            etDetallesPrecio.setText(ju.getPrecio().toString());
            ivDetallesJuegoImg.setImageBitmap(ju.getImagen());

            // no se permite cambiar la imagen del juego
            btnDetallesJuegoImagen.setVisibility(View.INVISIBLE);
            btnDetallesJuegoAdd.setText("Actualizar");
            Toast toast1 =
                    Toast.makeText(getContext(),
                            "Editar", Toast.LENGTH_SHORT);
            toast1.show();

            String[] plataformas = {"Nintendo Switch", "Sony PS4", "MS XBOX360", "PC", "Digital"};
            sDetallesPlataforma.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, plataformas));




            //evento del boton actualizar
            btnDetallesJuegoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagenFinal == null) {
                        imagenFinal = ju.getImagen();
                    }
                    // se comprueba que todo este relleno
                    if (etDetallesTitulo.getText().toString().equals("") || etDetallesFecha.getText().toString().equals("") || etDetallesPrecio.getText().toString().equals("") || imagenFinal == null || etDetallesPrecio.getText().toString().equals(".")) {

                        Toast.makeText(getActivity(), "¡Compruebe los campos por favor!", Toast.LENGTH_LONG).show();
                    } else {
                        //se comprueba que no se esten actualizando a un juego ya existennte en la misma plataforma
                        Juego j = new Juego(etDetallesTitulo.getText().toString(), sDetallesPlataforma.getSelectedItem().toString(), etDetallesFecha.getText().toString(), Float.parseFloat(etDetallesPrecio.getText().toString()), imagenFinal, ju.getId());
                        if (!comprobarRepetidos(etDetallesTitulo.getText().toString(), sDetallesPlataforma.getSelectedItem().toString())) {
                            Toast.makeText(getActivity(), "¡Juego Repetido en la misma plataforma!", Toast.LENGTH_SHORT).show();
                        } else {
                            //se actualiza
                            actualizarJuego(j);
                            Toast.makeText(getActivity(), "¡Juego Actualizado!", Toast.LENGTH_SHORT).show();
                        }


                    }


                }
            });

            //evento para elegir la fecha
            etDetallesFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.etDetalleJuegoFecha:
                            showDatePickerDialog();
                            break;
                    }
                }
            });
        }


        return root;
    }

    //Menú para volver a la lista de juegos
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detalles_juegos_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.it_detallesJuegos:
                getActivity().onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo para mostrar un dialog para seleccionar una fecha, despues se muestra en el EditText de fecha
     */
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
                etDetallesFecha.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    /**
     * Metodo para actualizar un juego dado
     *
     * @param j
     */
    private void actualizarJuego(Juego j) {
        //Abrimos la base de datos  en modo escritura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getWritableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            // Actualizamos el juego
            bd.execSQL("UPDATE JUEGOS SET titulo='" + j.getTitulo() + "', plataforma='" + j.getPlatamforma() + "', fecha='" + j.getFecha()
                    + "', precio=" + j.getPrecio() + ", imagen='" + bitmapToBase64(j.getImagen()) + "' WHERE id=" + j.getId());
        }
        //Cerramos la base de datos
        bd.close();
    }

    /**
     * Metodo para insertar un juego en la bbdd
     *
     * @param j
     */
    private void insertarData(Juego j) {
        //Abrimos la base de datos  en modo escritura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getWritableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            try {


                //para insertar la id cogemos la ultima id + 1
                Cursor c = bd.rawQuery(" SELECT MAX(id)   FROM Juegos", null);
                c.moveToFirst();
                int id = c.getInt(0);
                id++;
                //insertamos el juego recibido con su id
                bd.execSQL("INSERT INTO Juegos (id  , titulo , plataforma   , fecha  , precio  , imagen ) " +
                        "VALUES (" + id + ",'" + j.getTitulo() + "', '" + j.getPlatamforma() + "',' " + j.getFecha() + " '," + j.getPrecio() + ", '" + bitmapToBase64(j.getImagen()) + "')");

                //Cerramos la base de datos
                bd.close();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "¡ERROR EN LA BD!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Metodo para borrar un juego de la base de datos
     *
     * @param id
     */
    private void borrarData(int id) {
        //Abrimos la base de datos  en modo escritura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getWritableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            // Borramos el juego por su id
            bd.execSQL("DELETE FROM Juegos WHERE id=" + id);
        }
        //Cerramos la base de datos
        bd.close();
    }

    /**
     * Metodo para comprobar que un juego del mismo nombre y plataforma existan
     *
     * @param titulo_juego     titulo del juego
     * @param plataforma_juego plataforma del juego
     * @return
     */
    private boolean comprobarRepetidos(String titulo_juego, String plataforma_juego) {
        boolean repetido = true;

        //Abrimos la base de datos en modo lectura
        BDControlador MisJuegos = new BDControlador(this.getContext(), "MisJuegos", null, 1);
        SQLiteDatabase bd = MisJuegos.getReadableDatabase();
        //Si hemos abierto correctamente la base de datos
        if (bd != null) {
            //Seleccionamos todas las plataformas donde el titulo sea el que le hemos pasado
            Cursor c = bd.rawQuery(" SELECT plataforma  FROM Juegos Where titulo='" + titulo_juego + "'", null);
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    try {


                        String plata = c.getString(0);
                        //si la plataforma que hemos recibido y la que ha devuelto la select es igual es que está repetido
                        if (plataforma_juego.equals(plata)) {
                            repetido = false;
                            return repetido;
                        }

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "¡PROBLEMA AL CARGAR!", Toast.LENGTH_LONG).show();
                    }
                } while (c.moveToNext());
            }
            //Cerramos la base de datos
            bd.close();
        }


        return repetido;
    }

    /**
     * Dependiendo de la opcion elegida cogera una foto de camara o de galeria
     */
    private void mostrarDialogoFoto() {
        AlertDialog.Builder fotoDialogo = new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Seleccionar Acción");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara"};
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    /**
     * Dependiendo de la opcion indicada borrara un juego (pasandole la id del juego ) o no
     *
     * @param id
     */
    private void mostrarDialogoBorrar(final int id) {
        AlertDialog.Builder fotoDialogo = new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("¡Hey listen!");
        String[] fotoDialogoItems = {
                "Borrar Juego",
                "Cancelar"};
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                borrarData(id);
                                //una vez borrado vuelve a la lista de juegos
                                getActivity().onBackPressed();
                                break;
                            case 1:

                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    /**
     * Método para elegir una foto de la galeria
     */
    public void elegirFotoGaleria() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIA);

    }

    /**
     * Método para tomar una foto desde la camara
     */
    private void tomarFotoCamara() {

        try {


            // Si queremos hacer uso de fotos en aklta calidad
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            // Eso para alta o baja
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            // Esto para alta calidad
            // photoURI = Uri.fromFile(this.crearFichero());
            // intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);

            // Esto para alta y baja
            startActivityForResult(intent, CAMARA);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "¡Fallito wey!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo donde se recogen las imagenes tanto de camara como galeria y se procesan
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FOTO", "Opción::--->" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALERIA) {
            Log.d("FOTO", "Entramos en Galería");
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), contentURI);
                    //guardamos en un bitmap publico el bitmap cogido
                    imagenFinal = bitmap;

                    Toast.makeText(getActivity(), "¡Foto salvada!", Toast.LENGTH_SHORT).show();
                    //mostramos en el ImageView el bitmap seleccionado
                    this.ivDetallesJuegoImg.setImageBitmap(bitmap);
                    imagen = bitmapToBase64(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "¡Fallo Galeria!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMARA) {
            Log.d("FOTO", "Entramos en Camara");
            // Cogemos la imagen, pero podemos coger la imagen o su modo en baja calidad (thumbnail
            Bitmap thumbnail = null;
            try {
                try {


                    // Esta línea para baja
                    thumbnail = (Bitmap) data.getExtras().get("data");
                    // Esto para alta
                    // thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), photoURI);
                    imagenFinal = thumbnail;
                    // salvamos
                    // path = salvarImagen(thumbnail); //  photoURI.getPath(); Podríamos poner esto, pero vamos a salvarla comprimida y borramos la no comprimida (por gusto)
                    //mostramos en el ImageView el bitmap seleccionado
                    this.ivDetallesJuegoImg.setImageBitmap(thumbnail);
                    imagen = bitmapToBase64(thumbnail);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "¡Fallito wey!", Toast.LENGTH_SHORT).show();
                }
                // Borramos el fichero de la URI
                //borrarFichero(photoURI.getPath());

                Toast.makeText(getActivity(), "¡Foto Salvada!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "¡Fallo Camara!", Toast.LENGTH_SHORT).show();
            }

        }


    }


    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}

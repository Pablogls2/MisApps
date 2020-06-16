package com.example.rss.ui.carrusel;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rss.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;


public class CarruselFotos extends Fragment {
    private View root;
    private FeatureCoverFlow coverFlow;
    private AdaptadorImagenes adapter;

    private ArrayList<String> listaPaths = new ArrayList<String>();
    public CarruselFotos() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root= inflater.inflate(R.layout.fragment_carrusel_fotos, container, false);


        try {
            //pedimos los permisos para poder acceder a las fotos del dispositivo
            pedirMultiplesPermisos2();

            coverFlow = (FeatureCoverFlow) root.findViewById(R.id.coverflow);

            //llamamos al metodo para obtener las imagenes de la galeria
            obtenerImagenesGaleria();

            //creamos el adaptador de las imagenes con la lista de los Paths
            adapter = new AdaptadorImagenes( getActivity(), listaPaths, getFragmentManager() );
            coverFlow.setAdapter(adapter);
            coverFlow.setOnScrollPositionListener(onScrollListener());



        } catch (Exception e) {
            e.printStackTrace();
        }



        return  root;
    }

    /**
     * Metodo para el evento para interactuar con las imagenes
     * @return Listener OnScroll
     */
    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
            }
        };
    }

    /**
     * Método para obtener imagenes de la galeria
     */
    private void obtenerImagenesGaleria(){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        //el cursor obtiene todas las imagenes de la Uri, que tiene las carpetas de las imagenes
        cursor = getActivity().getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        //recorremos el cursor y vamos guardando los path de las imagenes en un array hasta que sea de longitud 5
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            if(listaPaths.size()<5 && !absolutePathOfImage.isEmpty()){
                listaPaths.add(absolutePathOfImage);
            }

        }
    }

    /**
     * Método para pedir permisos si no han sido aceptados al principio de la app
     */
    private void pedirMultiplesPermisos2() {
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(getActivity())
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
                        //Toast.makeText(this, "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

}

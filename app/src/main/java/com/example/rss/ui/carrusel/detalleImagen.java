package com.example.rss.ui.carrusel;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rss.BuildConfig;
import com.example.rss.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class detalleImagen extends Fragment {
    private  View root;
    private ImageView imagen;
    //private FloatingActionButton boton;
    String img=null;
    public detalleImagen() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_detalle_imagen, container, false);
        imagen=(ImageView) root.findViewById(R.id.ivDetalleCarrusel);

        //se recoge en un bundle los argumentos que se han pasado previamente
        Bundle b = getArguments();


        img=b.getString("imagen");
        Log.e("url","mira"+img);
        //se pasa la ImageView del layout la imagen parseada a uri
        imagen.setImageURI(Uri.parse(img));


        return  root;
    }


    //menu para volver atras y compartir la imagen
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detalle,menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // si es volver volvera al carrusel
            case R.id.it_Detalle_volver:
                getActivity().onBackPressed();
                break;

            //si es compartir llamara al metodo de compartir
            case R.id.it_Detalle_compartir:
                lanzar_compartir();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método para compartir una imagen
     */
    public void lanzar_compartir(){
        //se pasa a un BitmapDrawable y despues a un Bitmap normal la imagen
        BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        //se crea un Intent para compartir
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, img);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getActivity().getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }



}

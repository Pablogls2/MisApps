package com.example.rss.ui.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.rss.R;
import com.example.rss.ui.carrusel.AdaptadorImagenes;
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

import static android.app.Activity.RESULT_OK;

//clase para reproducir videos
public class Video extends Fragment {
   //atributos de la clase
    private View root;
    private ImageButton btnExaminar;
    private EditText etUrl;
    private final int PICKER=1;

    private MediaPlayer mediaPlayer;
    private VideoView svVideoReproductor;
    private SurfaceHolder surfaceHolder;
    private EditText editText;
    private ImageButton btnPlay;
    private TextView logTextView;
    private boolean pause;
    private String path;
    private int savePos = 0;




    public Video() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Iniciamos los componentes

        root= inflater.inflate(R.layout.fragment_video, container, false);

        btnExaminar= (ImageButton) root.findViewById(R.id.btnVideoExaminar);
        etUrl=(EditText) root.findViewById(R.id.etVideoUrl);
        svVideoReproductor= (VideoView) root.findViewById(R.id.vvVideoRepro);
        btnPlay=(ImageButton)root.findViewById(R.id.btnVideoPlay);



        //pedimos los permisos en caso de que no esten ya concedidos
        pedirMultiplesPermisos2();

        //al pulsar el boton de examinar llamara al metodo para coger un video
        btnExaminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cogerVideo();
            }
        });

        //se agrega al VideoView su controlador
        svVideoReproductor.setMediaController(new MediaController(getActivity()));

        //al pular el boton de play se ponda en el VideoView el path del video elegido y comenzara a reproducirse
        btnPlay.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                String url=etUrl.getText().toString();
                Log.e("mira ","e"+url);
                if(url.isEmpty()){

                    Toast.makeText(getActivity(),"SELECCIONE UN VIDEO", Toast.LENGTH_SHORT).show();
                }else{
                    svVideoReproductor.setVideoPath(url);
                    svVideoReproductor.start();
                    svVideoReproductor.requestFocus();
                }



            }
        });





        return  root;
    }

    /**
     * Método para coger un video de la galeria
     */
    private void cogerVideo(){
        //hace un intent de coger de la galeria , en concreto videos
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
         intent.setType("video/*");

        try{
            //lo empieza
            startActivityForResult(Intent.createChooser(intent,"VIDEO"),PICKER);

        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getActivity(),"Por favor, instale un admin de archivos", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Método que se activa al elegir un video
     */

    public  void onActivityResult (int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case PICKER:
                // al pulsar OK se guarda el path del video en un TextView
                if (resultCode==RESULT_OK){
                    Uri u= data.getData();

                    Log.e("mira ","e"+u);
                   String FilePath=getPath(u);

                    Log.e("mira ","e"+FilePath);
                    etUrl.setText(FilePath);
                }
            break;
        }
    }


    /**
     * Método para pedir permisos
     */
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


    /**
     * Método para devolver en forma de String un Uri de la galeria
     * @param uri Uri
     * @return String
     */
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {

            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    /**
     * Evento antes de destruirse el fragment al girar
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
       getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
}

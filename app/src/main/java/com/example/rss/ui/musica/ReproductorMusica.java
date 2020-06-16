package com.example.rss.ui.musica;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.rss.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;

// la clase necesita implementar la clase MediaController para mostrar unos controles básicos predefinidos
public class ReproductorMusica extends Fragment implements MediaController.MediaPlayerControl {

    //atributos de la clase
    private  View root;


    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private ArrayList<Song> songList;
    private ListView songView;
    private MusicController controller;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;


    private boolean paused=false, playbackPaused=false;

    public ReproductorMusica() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //para que tenga un menu
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inicializamos los componentes
        root= inflater.inflate(R.layout.fragment_reproductor_musica, container, false);


        songView = (ListView) root.findViewById(R.id.song_list);
        checkAndRequestPermissions();
        songList = new ArrayList<Song>();

        //metodo para rellenar la lista de canciones
        getSongList();

        // las ordenamos por titulo
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //creamos el adaptador de la lista para mostrarlas
        SongAdapter songAdt = new SongAdapter(root.getContext(), songList,this);
        songView.setAdapter(songAdt);
        //llamamos al metodo para mostrar el control basico
        setController();


        return root;
    }

    /**
     * Método propio de los servicios
     */
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /**
     * Método para rellenar la lista de canciones
     */
    public void getSongList() {
        //llenamos un cursor con los path de las canciones del dispositivo
        ContentResolver musicResolver = this.getContext().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // recorremos el cursor y rellenamos la lista
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    //para tener un menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_reproductor, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //en caso de pulsar aleatorio llamara al metodo del MusicService para activar o desactivarlo
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Al destruirse el fragment parara de sonar la cancion
     */
    @Override
    public void onDestroy() {

        this.getActivity().stopService(playIntent);
        musicSrv = null;

        super.onDestroy();
    }


    @Override
    public void onPause(){
        super.onPause();
        paused=true;
    }

    /**
     * Al volver al fragment se mostrara otra vez el controlador, es decir, los controles básicos
     */
    @Override
    public void onResume(){
        super.onResume();

        if(paused){
            controller.setVisibility(View.VISIBLE);
            paused=false;
        }
    }

    /**
     * Al pararse el fagment la cancion sigue sonando pero se ocula el controlador para no molestar al usuario
     */
    @Override
    public void onStop() {
        controller.setVisibility(View.GONE);

        super.onStop();
    }

    /**
     * Al lanzarse el evento onStart se inicializaran varios componentes del Servicio y se mostrara el controlador
     */
    public void onStart() {
        super.onStart();

        if (playIntent == null) {
            try {
                playIntent = new Intent(getActivity(), MusicService.class);
                this.getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                this.getActivity().startService(playIntent);
                controller.show();
            }catch (Exception ex){

            }
        }
    }


    // en caso de no haber aceptado los permisos se volveran a pedir
    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        String TAG = "LOG_PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions

                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        // process the normal flow

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission//shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this.getActivity().getBaseContext(), "Debes aceptar los permisos", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this.getActivity().getBaseContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    /**
     * Método para iniciar el controlador
     */
    private void setController() {
        controller = new MusicController(getContext());

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(root.findViewById(R.id.song_list));
        controller.setEnabled(true);
    }


    public boolean canPause() {
        return true;
    }



    public int getBufferPercentage() {
        return 0;
    }



    public boolean canSeekBackward() {
        return true;
    }


    public boolean canSeekForward() {
        return true;
    }

    /**
     * Metodo para coger la posicion actual
     * @return int
     */
    public int getCurrentPosition() {
        if (musicSrv != null && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }


    public int getAudioSessionId() {
        return 0;
    }

    /**
     * Método de pasar a la siguiente cancion
     */
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }
    /**
     * Método de pasar a la anterior cancion
     */
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    /**
     * Método de elegir una cancion
     * @param view view
     */
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
           // setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    /**
     * Método para pausar la cancion
     */
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
    }

    /**
     * Método para coger la duracion de la cancion
     * @return int
     */
    public int getDuration() {
        if (musicSrv != null && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    /**
     * Método para saber si se esta escuchando una cancion
     * @return
     */
    public boolean isPlaying() {
        if (musicSrv != null) return musicSrv.isPng();
        return false;
    }


    /**
     * Método para ir a un a posicion dada
     * @param pos int
     */
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    /**
     * Método para empezar el servicio
     */
    public void start() {
        musicSrv.go();
    }


}

package com.example.rss.ui.musica;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.rss.R;

import java.util.ArrayList;
import java.util.Random;

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {


    private final IBinder musicBind = new MusicBinder();
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private String songTitle="&quot;&quot;";
    private static final int NOTIFY_ID=1;

    private boolean shuffle=false;
    private Random rand;


    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onCreate() {
        //crear el servicio
        super.onCreate();
        //inicializamos la posicion
        songPosn = 0;
        //creamos el reproductor
        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();
    }

    /**
     * Método para poner el modo de reproduccion aleatoria
     */
    public void setShuffle(){
        if(shuffle){
            shuffle=false;
            Toast.makeText(getBaseContext(),"MODO ALEATORIO DESACTIVADO", Toast.LENGTH_LONG).show();
        }
        else{
            shuffle=true;
            Toast.makeText(getBaseContext(),"MODO ALEATORIO ACTIVADO", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Método para inicializar el reporoductor de música
     */
    public void initMusicPlayer() {
       player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();
        Intent notIntent = new Intent(this, ReproductorMusica.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(songTitle)
  .setContentText(songTitle);
        Notification not = builder.build();


        getNotification();
    }



    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("snap map fake location");
        Notification notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }





    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    /**
     * Método para empezar una canción
     */
    public void playSong() {
        player.reset();

        //cogemos la cancion de la posicion dada
        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
        //cogemos su id
        long currSong = playSong.getID();
        //ponemos la uri para buscarla en el dispositivo
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);


        try{
            //buscamos la cancion
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    /**
     * Método al destruirse la cancion
     */
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    /**
     * Método para poner una cancion
     * @param songIndex
     */
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    /**
     * Método que se lanza al saltar un error
     * @param mp Reproductor
     * @param what int
     * @param extra int
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    /**
     * Método para coger la posicion
     * @return
     */
    public int getPosn(){
        return player.getCurrentPosition();
    }

    /**
     * Método para coger la duracion de la cancion
     * @return int ,duracion
     */
    public int getDur(){
        return player.getDuration();
    }

    /**
     * Método para comprobar que se está reproduciendo una cancion
     * @return boolean
     */
    public boolean isPng(){
        return player.isPlaying();
    }

    /**
     * Método para pausar una cancion
     */
    public void pausePlayer(){
        player.pause();
    }

    /**
     * Método para pasar a una posicion dada
     * @param posn int
     */
    public void seek(int posn){
        player.seekTo(posn);
    }

    /**
     * Método para comenzar el MediaPlayer
     */
    public void go(){
        player.start();
    }

    /**
     * Método para cambiar a la cancion anterior
     */
    public void playPrev(){
        songPosn--;
        if(songPosn==0) songPosn=songs.size()-1;
        playSong();
    }


    /**
     * Método para pasar a la siguiente cancion
     */
    public void playNext(){
        // si esta activado el modo aleatorio la nueva cancion sera de una posicion aleatoria
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn==songs.size()) songPosn=0;
        }
        playSong();
    }

    /**
     * Una vez termina una cancion pasara automaticamente a la siguiente
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()==0){
            mp.reset();
            playNext();
        }
    }


}

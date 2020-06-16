package com.example.rss.ui.musica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rss.R;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private  Context c;
    ReproductorMusica rm;

    public SongAdapter(Context c, ArrayList<Song> theSongs, ReproductorMusica rm){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
        this.c=c;
        this.rm=rm;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //layout para mostrar las canciones
        final LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.song, parent, false);
        //iniciamos los componentes para mostrar los datos de la cancion
        TextView songView = (TextView)songLay.findViewById(R.id.tvSongTitulo);
        TextView artistView = (TextView)songLay.findViewById(R.id.tvSongArtista);

        //cogemos la cancion con su posicion
        Song currSong = songs.get(position);
        //cogemos el titulo y el artista y lo mostramos
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //ponemos la cancion como un tag
        songLay.setTag(position);

       
        //listener para cuando el usuario pinche la cancion la recoga el ReproductorMusica
        songLay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                rm.songPicked(v);
            }
        });

        return songLay;
    }
}

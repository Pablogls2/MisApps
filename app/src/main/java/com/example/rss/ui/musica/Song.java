package com.example.rss.ui.musica;
//Clase para guardar la informacion de las canciones
public class Song {
    //atributos
    private long id;
    private String title;
    private String artist;

    //constructor
    public Song(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
}

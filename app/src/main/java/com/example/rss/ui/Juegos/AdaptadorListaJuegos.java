package com.example.rss.ui.Juegos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rss.ImagenRedonda;
import com.example.rss.R;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AdaptadorListaJuegos extends RecyclerView.Adapter<AdaptadorListaJuegos.ViewHolder> {
    //Atributos de la clase
    private ArrayList<Juego> lista;
    private Context context;
    private Bundle b;
    private Activity ac;
    private Juego juego;
    private FragmentManager fm;

    //cosntructor
    public AdaptadorListaJuegos(ArrayList<Juego> lista, Context context, Activity ac, FragmentManager fm) {
        this.lista = lista;
        this.context = context;
        this.ac = ac;
        this.fm = fm;
    }

    // Le pasamos el ViewHolder y el layout que va a usar
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_juegos, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    // Publicamos el evento en la posición del holder y lo programamos
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //creamos  los juegos que hayan en la lista de juegos
        juego = (Juego) lista.get(position);
        //si el titulo del juego es muy largo le pondremos unos ...
        if (juego.getTitulo().length() > 50) {
            String recorte = juego.getTitulo().substring(0, 50);
            holder.titulo.setText(recorte + "...");
        } else {
            holder.titulo.setText(juego.getTitulo());
        }

        holder.precio.setText((juego.getPrecio().toString()));

        //ponemos la imagen escalada a 300x300
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(juego.getImagen(), 300, 300, false));

        holder.plataforma.setText(juego.getPlatamforma());

        holder.fecha.setText(juego.getFecha());


        // Cargamos los eventos de los componentes que quedamos
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //cuando pinche se ira al fragment de detalles para ver el juego
                juego = (Juego) lista.get(position);
                DetallesJuego_fragment de = DetallesJuego_fragment.newInstance("ver", juego);
                FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, de);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

    //nos lleva al fragment para borrar el juego
    public void borrarDatos(int posicion) {
        juego = (Juego) lista.get(posicion);
        DetallesJuego_fragment de = DetallesJuego_fragment.newInstance("borrar", juego);
        FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, de);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //nos lleva al fragment para editar
    public void editarJuegos(int posicion) {
        juego = (Juego) lista.get(posicion);
        DetallesJuego_fragment de = DetallesJuego_fragment.newInstance("editar", juego);
        FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, de);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public int getItemCount() {
        return lista.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // componentes que vamos a manejar
        public ImageView imageView;
        public TextView titulo;
        public TextView fecha;
        public TextView precio;
        public TextView plataforma;

        // Layout de la fila
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.ivListJuegosImagen);
            this.titulo = (TextView) itemView.findViewById(R.id.tvListJuegosTitulo);
            this.fecha = (TextView) itemView.findViewById(R.id.tvListJuegosFecha);
            this.precio = (TextView) itemView.findViewById(R.id.tvListJuegosPrecio);
            this.plataforma = (TextView) itemView.findViewById(R.id.tvListJuegosPlataforma);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeListJuegos);
        }
    }
}

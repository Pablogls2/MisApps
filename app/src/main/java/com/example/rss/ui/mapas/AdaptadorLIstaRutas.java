package com.example.rss.ui.mapas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rss.AdaptadorRecycled;
import com.example.rss.ImagenRedonda;
import com.example.rss.Noticia;
import com.example.rss.R;
import com.example.rss.ui.detalles.DetallesFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorLIstaRutas extends RecyclerView.Adapter<AdaptadorLIstaRutas.ViewHolder>{

    // Array list que le pasamos
    private ArrayList<String> listdata;
    private Context context;
    private Bundle b;
    //private Activity ac;
    private Activity ac;
    private FragmentManager fm;

    // RecyclerView recyclerView; ---
    // Cponstructor

    public AdaptadorLIstaRutas(ArrayList<String> listdata, Context context,Activity ac, FragmentManager fm) {
        this.context = context;
        this.listdata = listdata;
        this.ac=ac;
        this.fm=fm;
    }


    @Override
    // Le pasamos el ViewHolder y el layout que va a usar
    public AdaptadorLIstaRutas.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.song, parent, false);
        AdaptadorLIstaRutas.ViewHolder viewHolder = new AdaptadorLIstaRutas.ViewHolder(listItem);
        return viewHolder;
    }


    // Publicamos el evento en la posición del holder y lo programamos
    // Ver el ejemplo de la lista
    public void onBindViewHolder(AdaptadorLIstaRutas.ViewHolder holder, final int position) {


        holder.ruta.setText(listdata.get(position).substring(listdata.get(position).lastIndexOf("/")+1));



        // Cargamos los eventos de los componentes que quedamos
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mapa de= new mapa(true);
                //para meter en un fragment info
                b = new Bundle();


                b.putString("ruta", listdata.get(position));
                de.setArguments(b);

                FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, de);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }





    @Override
    public int getItemCount() {
        return listdata.size();
    }

    // Aqui está el holder y lo que va a manejar, es decir la vista para interactuar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // componentes que vamos a manejar

        public TextView ruta;


        // Layout de la fila
        public LinearLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            this.ruta = (TextView) itemView.findViewById(R.id.tvSongTitulo);
            relativeLayout = (LinearLayout) itemView.findViewById(R.id.linear_song);
            relativeLayout.setBackgroundColor(Color.BLUE);
        }
    }
}

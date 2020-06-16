package com.example.rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rss.ui.acercaDe.AcercaDeFragment;
import com.example.rss.ui.detalles.DetallesFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorRecycled extends RecyclerView.Adapter<AdaptadorRecycled.ViewHolder> {


    // Array list que le pasamos
    private ArrayList<Noticia> listdata;
    private Context context;
    private Bundle b;
    //private Activity ac;
    private Activity ac;
    private  FragmentManager fm;

    // RecyclerView recyclerView; ---
    // Cponstructor

    public AdaptadorRecycled(ArrayList<Noticia> listdata, Context context,Activity ac, FragmentManager fm) {
        this.context = context;
        this.listdata = listdata;
        this.ac=ac;
        this.fm=fm;
    }


    @Override
    // Le pasamos el ViewHolder y el layout que va a usar
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override

    // Publicamos el evento en la posición del holder y lo programamos
    // Ver el ejemplo de la lista
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String fecha = null;

        final Noticia not = (Noticia) listdata.get(position);
        if (not.getTitular().length() > 50) {
            String recorte = not.getTitular().substring(0, 50);
            holder.titulo.setText(recorte + "...");
        } else {
            holder.titulo.setText(not.getTitular());
        }

        try {


            Picasso.with(context).load(not.getImagen()).transform(new ImagenRedonda()).into(holder.imageView);
        } catch (Exception e) {

        }
        //para hacer un formato entendible con un substring cojo la fecha por separado
        String fech = not.getFecha().substring(5, 17);
        StringBuffer sr = new StringBuffer();
        sr.append(fech);
        //la voy pegando con un stringbuffer , con una tabulacion la separo de la hora
        sr.append("\n");
        sr.append(not.getFecha().substring(17, 22));


        holder.fecha.setText(sr.toString());


        // Cargamos los eventos de los componentes que quedamos
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                verNoticia(position);

            }
        });
    }

    public void removeItem(int position) {
        listdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listdata.size());


    }

    public void restoreItem(Noticia item, int position) {
        //listdata.set(position, item);
        listdata.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listdata.size());
    }

    public void verNoticia(int position) {


         //FragmentManager fm = ((FragmentActivity)ac).getSupportFragmentManager();

        DetallesFragment de= new DetallesFragment();
        //para meter en un fragment info
        b = new Bundle();
        final Noticia noticia;
        noticia = listdata.get(position);

        b.putSerializable("noticia", noticia);
        de.setArguments(b);



        FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, de);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        notifyItemRangeChanged(position, listdata.size());

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    // Aqui está el holder y lo que va a manejar, es decir la vista para interactuar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // componentes que vamos a manejar
        public ImageView imageView;
        public TextView titulo;
        public TextView fecha;

        // Layout de la fila
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.Lista_Imagen);
            this.titulo = (TextView) itemView.findViewById(R.id.listaHomeTitulo);
            this.fecha = (TextView) itemView.findViewById(R.id.listaHomeFecha);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}

package com.example.rss.ui.carrusel;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rss.R;

import java.util.ArrayList;


public class AdaptadorImagenes extends BaseAdapter {

    //atributos de la clase
    private ArrayList<String> IMAGES;
    private Activity activity;
    private FragmentManager fm;

    //constructor de la clase
    public AdaptadorImagenes(Activity context, ArrayList<String> objects, FragmentManager fm) {
        this.activity = context;
        this.IMAGES = objects;
        this.fm=fm;
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    public String getItem(int position) {
        return IMAGES.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        // si la vista no es null se inicializan los componentes , como layouts y el viewHolder necesario
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.slidingimages_layout, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //el viewHolder pone la imagen de la lista de la posicion dada en el ImageView del layout
        viewHolder.gameImage.setImageURI(Uri.parse(IMAGES.get(position)));

        // al hacer click en la imagen llamara a otro fragment
        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // se crea un bundle para guardar la imagen
               Bundle b = new Bundle();
               b.putString("imagen",IMAGES.get(position));
                //se pasa al fragment detalle
               detalleImagen d= new detalleImagen();
               d.setArguments(b);

                //se abre el fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.nav_host_fragment, d);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        };
    }


    private static class ViewHolder {

        private ImageView gameImage;

        public ViewHolder(View v) {
            //se inicializa el ImageView
            gameImage = (ImageView) v.findViewById(R.id.ivSlidingImagen);

        }
    }

}

package com.example.rss.ui.detalles;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rss.BuildConfig;
import com.example.rss.Noticia;
import com.example.rss.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class DetallesFragment extends Fragment {

    private DetallesViewModel mViewModel;
    private TextView tvDetalleTitulo;
    private TextView tvDetalleFecha;
    private TextView tvDetalleAutor;
    private ImageView ivDetalleImagen;
    private WebView wvDetalleWeb;
    private FloatingActionButton fabDetalleBoton;
    private Noticia n;

    private View root;

    public static DetallesFragment newInstance() {
        return new DetallesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         root= inflater.inflate(R.layout.detalles_fragment, container, false);

        root.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                return true;
            }
        });



        iniciarVista();

        //para que no se pueda girar la pantalla
        this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //se inician los elementos
        iniciarVista();

        //se recoge en un bundle la noticia que ha sido mandada
        Bundle b = this.getArguments();

        n = (Noticia) b.getSerializable("noticia");
        //se pone el titulo
        tvDetalleTitulo.setText(n.getTitular());

        //para iniciar la fecha con formato
        fecha(n);
        //se pone la imagen
        Picasso.with(this.getActivity()).load(n.getImagen()).into(this.ivDetalleImagen);
        //en el caso de que halla autor se muestra
        if (!n.getAutor().equals("")) {
            tvDetalleAutor.setText(n.getAutor());
        }

        //se carga el WebView con la descripicion ya que viene en formato html
        this.wvDetalleWeb.loadDataWithBaseURL("", n.getDescripcion(), "text/html", "UTF-8", "");

        //funcionalidad del boton flotante para abrir el enlace de la noticia
        this.fabDetalleBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(n.getEnlace());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


        return  root;
    }
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detalle,menu);
    }





    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_Detalle_volver:
                getActivity().onBackPressed();
                break;


            case R.id.it_Detalle_compartir:
                lanzar_compartir();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    public void fecha(Noticia n) {
        String fech = n.getFecha().substring(5, 17);
        StringBuffer sr = new StringBuffer();
        sr.append(fech);
        //la voy pegando con un stringbuffer , con una tabulacion la separo de la hora
        sr.append("\n");
        sr.append(n.getFecha().substring(17, 22));


        this.tvDetalleFecha.setText(sr.toString());
    }

    public void lanzar_compartir() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My App");
            String shareMessage = "\nEy mira esta noticia tan interesante\n\n";
            shareMessage = shareMessage + n.getEnlace() + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }
    public void iniciarVista() {
        this.tvDetalleTitulo = (TextView) root.findViewById(R.id.tvDetallesTitulo);
        this.tvDetalleFecha = (TextView) root.findViewById(R.id.tvDetallesFecha);
        this.tvDetalleAutor = (TextView) root.findViewById(R.id.tvDetallesAutor);
        this.ivDetalleImagen = (ImageView) root.findViewById(R.id.ivDetallesImagen);
        this.wvDetalleWeb = (WebView) root.findViewById(R.id.wvDetallesWeb);
        this.fabDetalleBoton = (FloatingActionButton) root.findViewById(R.id.fabDetallesboton);
    }

}

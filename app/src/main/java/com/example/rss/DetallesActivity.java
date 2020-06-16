package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class DetallesActivity extends AppCompatActivity {
    private TextView tvDetalleTitulo;
    private TextView tvDetalleFecha;
    private TextView tvDetalleAutor;
    private ImageView ivDetalleImagen;
    private WebView wvDetalleWeb;
    private FloatingActionButton fabDetalleBoton;
    private Noticia n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //para que no se pueda girar la pantalla
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //se inician los elementos
        iniciarVista();

        //se recoge en un bundle la noticia que ha sido mandada
        Bundle b = this.getIntent().getExtras();

        n = (Noticia) b.getSerializable("noticia");
        //se pone el titulo
        tvDetalleTitulo.setText(n.getTitular());

        //para iniciar la fecha con formato
        fecha(n);
        //se pone la imagen
        Picasso.with(this).load(n.getImagen()).into(this.ivDetalleImagen);
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


    }

    public void iniciarVista() {
        this.tvDetalleTitulo = (TextView) findViewById(R.id.tvDetallesTitulo);
        this.tvDetalleFecha = (TextView) findViewById(R.id.tvDetallesFecha);
        this.tvDetalleAutor = (TextView) findViewById(R.id.tvDetallesAutor);
        this.ivDetalleImagen = (ImageView) findViewById(R.id.ivDetallesImagen);
        this.wvDetalleWeb = (WebView) findViewById(R.id.wvDetallesWeb);
        this.fabDetalleBoton = (FloatingActionButton) findViewById(R.id.fabDetallesboton);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detalle, menu);


        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;


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

}

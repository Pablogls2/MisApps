package com.example.rss.ui.mapas;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.rss.AdaptadorRecycled;
import com.example.rss.Noticia;
import com.example.rss.R;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//fragment para ver las rutas guardadas en el dispositivo
public class verRutas extends Fragment {
    //atributos
    private View root;
    private ArrayList<String> rutas;
    private AdaptadorLIstaRutas adapter;
    private RecyclerView recyclerView;

    public verRutas() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_ver_rutas, container, false);
        recyclerView= (RecyclerView)root.findViewById(R.id.recyclerVerRutas);
        //arraylist para guardar las rutas del dispositivo
        rutas = new ArrayList<>();


        //Asyn
        class AsynRuta extends AsyncTask<Void, Void, ArrayList<String >> {



            //en el metodo doInBackground se van guardando en un ArrayList de String las diferentes rutas guardadas
            public ArrayList<String> doInBackground(Void... params) {
                //accedemos al almacenamiento publico
                String sdcard = Environment.getExternalStorageDirectory().toString();


                File direc = new File(sdcard);
                File[] files = direc.listFiles();


                for (File f : files) {
                    //cogemos solo los ficheros que acaban en .xml
                    if (f.isFile() && f.getPath().endsWith(".xml")) {
                        //vamos guardando las rutas en el
                        rutas.add(f.getPath());
                        Log.e("rutas", "a" + f.getPath());
                    }

                }


                return rutas;
            }


            protected void onPostExecute(ArrayList <String > lista) {


                //Log.e("rutaas", "a" + rutas.get(1));

                //se carga en el adapadator la lista rellena de rutas y se le pasa el contexto del fragment
                adapter = new AdaptadorLIstaRutas(rutas, getContext(), getActivity(), getFragmentManager());
                recyclerView.setHasFixedSize(true);
                // se presenta en formato lineal
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                //se le aplica el adaptador al recyclerView
                recyclerView.setAdapter(adapter);


            }


        }
        //se ejecuta la tarea asincrona
        new AsynRuta().execute();


        return root;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_volver,menu);

    }





    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_volver:
                getActivity().onBackPressed();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


}

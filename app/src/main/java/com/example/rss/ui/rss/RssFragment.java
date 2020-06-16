package com.example.rss.ui.rss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rss.AdaptadorRecycled;
import com.example.rss.Noticia;
import com.example.rss.R;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class RssFragment extends Fragment {
    private ArrayList<Noticia> listaNoticias;

    private RssViewModel rssViewModel;
    private View root;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Paint paint = new Paint();
    ConstraintLayout constr;
    private AdaptadorRecycled adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rssViewModel =
                ViewModelProviders.of(this).get(RssViewModel.class);
        root = inflater.inflate(R.layout.fragment_rss, container, false);

        //se inicializan los componentes
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerHomeLista);
        constr = (ConstraintLayout) root.findViewById(R.id.constraint);
        listaNoticias = new ArrayList<Noticia>();

        //tarea asincrona para cargar en el RecyclerView las noticias desde un feed RSS
        class AsynRss extends AsyncTask<Void, Void, ArrayList<Noticia>> {
            //direccion del feed RSS, en mi caso de las ultimas noticias deportivas del As
            String direccion = "https://as.com/rss/tags/ultimas_noticias.xml";

            //en el metodo doInBackground se van guardando en un ArrayList de noticias las diferentes noticias
            public ArrayList<Noticia> doInBackground(Void... params) {
                //
                String titulo = "";
                String enlace = "";
                String descripcion = "";
                String imagen = "";
                String fecha = "";
                String autor = "";


                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {

                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document document = builder.parse(direccion);

                    NodeList items = document.getElementsByTagName("item");
                    //se van recorriendo los items del rss y se guarda en un objeto Noticia el titulo, autor,enlace,descripcion,imagen y fecha
                    for (int i = 0; i < items.getLength(); i++) {
                        Node nodo = items.item(i);
                        for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {
                            if (n.getNodeName().equals("title")) {

                                titulo = n.getTextContent();

                            }
                            //Log.e("nodo", " a" + titulo);

                            if (n.getNodeName().equals("link")) {
                                enlace = n.getTextContent();
                            }
                            if (n.getNodeName().equals("content:encoded")) {
                                descripcion = n.getTextContent();

                                descripcion = descripcion.replaceAll("<[^>]*>", "");
                            }
                            if (n.getNodeName().equals("enclosure")) {

                                Element e = (Element) n;
                                String g = e.getAttribute("url");
                                imagen = g;

                            }
                            if (n.getNodeName().equals("pubDate")) {
                                fecha = n.getTextContent();
                            }
                            if (n.getNodeName().equals("dc:creator")) {
                                autor = n.getTextContent();
                            }

                        }

                        Noticia noticia = new Noticia(titulo, descripcion, enlace, imagen, fecha, autor);
                       // Log.e("tituloppp", " a" + noticia.getAutor());
                        //control para que no se cree una lista demasiada extensa
                        if (listaNoticias.size() <= 15) {
                            listaNoticias.add(noticia);
                        }


                    }

                } catch (ParserConfigurationException e) {
                    System.err.println("No se pudo crear una instancia de DocumentBuilder");
                } catch (SAXException e) {
                    System.err.println("Error SAX al parsear el archivo");
                } catch (IOException e) {
                    System.err.println("Se produjo un error de E/S");
                } catch (DOMException e) {
                    System.err.println("Se produjo un error del DOM");

                }


                //
                return listaNoticias;
            }


            protected void onPostExecute(ArrayList<Noticia> lista) {
                //se carga en el adapadator la lista rellena de noticias y se le pasa el contexto del fragment
                adapter = new AdaptadorRecycled(lista, getContext(),getActivity(),getFragmentManager());
                recyclerView.setHasFixedSize(true);
                // se presenta en formato lineal
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                //se le aplica el adaptador al recyclerView
                recyclerView.setAdapter(adapter);


            }


        }
        //se ejecuta la tarea asincrona
        new AsynRss().execute();

        //se inicia el deslizamiento horizontal
        iniciarSwipeHorizontal();
        //se inicia el deslizamiento vertical
        iniciarSwipeRecargar();


        return root;
    }

    /**
     * Metodo para iniciar el deslizamiento horizontal
     */
    private void iniciarSwipeHorizontal() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            // Evento al mover
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                // Si nos movemos a la izquierda
                if (direction == ItemTouchHelper.LEFT) {
                    borrarElemento(position);
                    /*final Noticia deletedModel = noticias.get(position);
                    final int deletedPosition = position;
                    //adapter.removeItem(position);
                    // Mostramos la barra


                    Snackbar snackbar = Snackbar.make(clRssCord, "Se ha eliminado el 1 elemento de la lista", Snackbar.LENGTH_LONG);
                    snackbar.setAction("DESHACER", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            adapter.restoreItem(deletedModel, deletedPosition);
                        }
                    });
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();*/


                    // Si es a la derecha
                } else {
                    verNoticia(position);
                }
            }

            // Con este codigo se dibujarian los iconos y efectos al deslizar pero da fallo, en algunos terminales funciona y en otros no
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // Si es dirección a la derecha: izquierda->derecta
                    if (dX > 0) {
                        paint.setColor(Color.YELLOW);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lupa);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);

                        // Caso contrario
                    } else {
                        paint.setColor(Color.RED);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Metodo para borrar un elemento de la lista de noticias (al deslizar)
     *
     * @param position
     */
    private void borrarElemento(int position) {
        //se guarda la Noticia y la posicion donde estaba
        final Noticia noticiaAborrar = listaNoticias.get(position);
        final int posicionAborrar = position;
        // se llama al metodo para borrar la noticia en el adaptador del ReciclerView
        adapter.removeItem(position);
        // Mostramos la barra para poder deshacer
        Snackbar snackbar = Snackbar.make(constr, " eliminado de la lista de noticias", Snackbar.LENGTH_LONG);
        snackbar.setAction("DESHACER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                adapter.restoreItem(noticiaAborrar, posicionAborrar);
            }
        });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    /**
     * Metodo para ver la noticia
     *
     * @param position
     */
    private void verNoticia(int position) {
        // se llama al metodo del adaptador
        adapter.verNoticia(position);

    }

    /**
     * Metodo para que al deslizar se recarguen las noticias
     */
    private void iniciarSwipeRecargar() {
        // Para refrescar y volver al cargar

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // se asignan los colores
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.fondoProgress);
                // Volvemos a cargar los datos
                new AsynRss2().execute();


            }
        });
    }

    /**
     * tarea asincrona parecida a la primera pero con la variante de la barra de progreso
     */
    class AsynRss2 extends AsyncTask<Void, Void, ArrayList<Noticia>> {

        //direccion del pais:http://ep00.epimg.net/rss/elpais/portada.xml
        //direccion del as: https://as.com/rss/tags/ultimas_noticias.xml
        String direccion = "https://as.com/rss/tags/ultimas_noticias.xml";

        private ProgressBar progressBar;

        protected void onPreExecute() {
            // Saco la barra de progreso
            progressBar = root.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }


        public ArrayList<Noticia> doInBackground(Void... params) {
            //
            String titulo = "";
            String enlace = "";
            String descripcion = "";
            String imagen = "";
            String fecha = "";
            String autor = "";


            listaNoticias = new ArrayList();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(direccion);

                NodeList items = document.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    Node nodo = items.item(i);
                    for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {
                        if (n.getNodeName().equals("title")) {

                            titulo = n.getTextContent();

                        }
                        Log.e("nodo", " a" + titulo);

                        if (n.getNodeName().equals("link")) {
                            enlace = n.getTextContent();
                        }
                        if (n.getNodeName().equals("content:encoded")) {
                            descripcion = n.getTextContent();

                            descripcion = descripcion.replaceAll("<[^>]*>", "");
                        }
                        if (n.getNodeName().equals("enclosure")) {

                            Element e = (Element) n;
                            String g = e.getAttribute("url");
                            imagen = g;

                        }
                        if (n.getNodeName().equals("pubDate")) {
                            fecha = n.getTextContent();
                        }
                        if (n.getNodeName().equals("dc:creator")) {
                            autor = n.getTextContent();
                        }

                    }

                    Noticia noticia = new Noticia(titulo, descripcion, enlace, imagen, fecha, autor);
                    Log.e("hiiiiilo", "e" + titulo);
                    if (listaNoticias.size() <= 15) {
                        listaNoticias.add(noticia);
                    }


                }

            } catch (ParserConfigurationException e) {
                System.err.println("No se pudo crear una instancia de DocumentBuilder");
            } catch (SAXException e) {
                System.err.println("Error SAX al parsear el archivo");
            } catch (IOException e) {
                System.err.println("Se produjo un error de E/S");
            } catch (DOMException e) {
                System.err.println("Se produjo un error del DOM");

            }


            //
            return listaNoticias;
        }


        protected void onPostExecute(ArrayList<Noticia> lista) {
            // Lo cargamos
            //quitamos la barra de progreso
            progressBar.setVisibility(View.GONE);
           // recyclerView = (RecyclerView) root.findViewById(R.id.recyclerHomeLista);
            adapter = new AdaptadorRecycled(lista, getContext(),getActivity(),getFragmentManager());
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

            recyclerView.setAdapter(adapter);
            //dejamos de refrescar
            swipeRefreshLayout.setRefreshing(false);


        }


    }


}
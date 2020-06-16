package com.example.rss.ui.acercaDe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.rss.R;

public class AcercaDeFragment extends Fragment {

    private AcercaDeViewModel acercaDe;
    private TextView tvAcercadeImagen;
    private ImageView ivAcercaImagen;
    private Button btnAcercadeTwitter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        acercaDe =
                ViewModelProviders.of(this).get(AcercaDeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_acercade, container, false);

        //Boton para llevar al usuario al portal de Twitter del creador
        btnAcercadeTwitter = root.findViewById(R.id.btnAcercaTwitter);

        btnAcercadeTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://twitter.com/pablopradoruiz");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return root;
    }
}
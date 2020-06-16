package com.example.rss.ui.Contacto;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rss.R;

import java.util.regex.Pattern;

public class ContactoFragment extends Fragment {

    private ContactoViewModel mViewModel;

    private TextView tvContactoNombre;
    private TextView tvContactoCorreo;
    private EditText etContactoNombre;
    private EditText etContactoCorreo;
    private CheckBox cbContactoCheck;
    private Button btnContactoEnviar;

    public static ContactoFragment newInstance() {
        return new ContactoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contacto_fragment, container, false);

        this.tvContactoNombre = (TextView) root.findViewById(R.id.tvContactoNombre);
        this.tvContactoCorreo = (TextView) root.findViewById(R.id.tvContactoCorreo);
        this.etContactoNombre = (EditText) root.findViewById(R.id.etContactoNombre);
        this.etContactoCorreo = (EditText) root.findViewById(R.id.etContactoEmail);
        this.btnContactoEnviar = (Button) root.findViewById(R.id.btnContactoEnviar);
        this.cbContactoCheck = (CheckBox) root.findViewById(R.id.cbContactoCheck);


        //accion del boton enviar que tras comprobar los campos envia el correo con el nombre del usuario
        btnContactoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbContactoCheck.isChecked() && validarEmail(etContactoCorreo.getText().toString()) && !etContactoNombre.getText().toString().equals("")) {
                    sendEmail();
                } else {

                }


            }
        });

        return root;

    }

    /**
     * Metodo para enviar el email
     */
    protected void sendEmail() {
        String[] TO = {etContactoCorreo.getText().toString()}; //aquí pon tu correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, etContactoNombre.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, etContactoNombre.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast toast1 =
                    Toast.makeText(ContactoFragment.this.getActivity(),
                            "Error al enviar", Toast.LENGTH_SHORT);
        }
    }

    /**
     * metodo para validar el campo del email
     *
     * @param email
     * @return
     */
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


}

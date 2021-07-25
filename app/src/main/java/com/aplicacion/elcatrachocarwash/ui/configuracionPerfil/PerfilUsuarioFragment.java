package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.elcatrachocarwash.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ObjectInput;

public class PerfilUsuarioFragment extends Fragment{

     ImageView img;
     EditText ttnombre, ttapellido, ttpais,ttemail;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    ImageButton btn_sesion;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);
        ttnombre = (EditText)view.findViewById(R.id.ttnombre);
        ttapellido = (EditText)view.findViewById(R.id.ttapellido);
        ttpais = (EditText)view.findViewById(R.id.ttpais);
        ttemail = (EditText)view.findViewById(R.id.ttemail);
        img = (ImageView) view.findViewById(R.id.img);





        return view;




    }

    public interface OnFragmentInteractionListener {

    }
}
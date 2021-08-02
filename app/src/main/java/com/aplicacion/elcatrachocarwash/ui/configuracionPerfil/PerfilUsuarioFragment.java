package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplicacion.elcatrachocarwash.LoginActivity;
import com.aplicacion.elcatrachocarwash.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilUsuarioFragment extends Fragment {

     ImageView img;
     TextView ttnombre, ttemail, ttpais;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    ImageButton btn_cerrarsesion;
    String name, email;
    Uri photoUrl;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        btn_cerrarsesion = (ImageButton)view.findViewById(R.id.btn_cerrarsesion);
        ttnombre = (TextView)view.findViewById(R.id.ttnombre);
        ttpais = (TextView)view.findViewById(R.id.ttpais);
        ttemail = (TextView)view.findViewById(R.id.ttemail);
        img = (ImageView)view.findViewById(R.id.img);

        //INICIALIZAR FIREBASE PARA OBTENER EL USUARIO ACTUAL
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // QUITAAAAAAAAAAAAAAAAAAR
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();
        }

        ttnombre.setText(name);
        ttemail.setText(email);

        //cargar imágen con glide:
        Glide.with(this).load(photoUrl);




        btn_cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CERRAR SESION EN FIREBASE
                mAuth.signOut();

                //cerrar son google
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Abrir MainActivity con SigIn button
                        if(task.isSuccessful()){
                            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            PerfilUsuarioFragment.this.getActivity().finish();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "No se pudo cerrar sesión con google",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return view;






    }


}
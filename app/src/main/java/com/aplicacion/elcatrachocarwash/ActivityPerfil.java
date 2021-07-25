package com.aplicacion.elcatrachocarwash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityPerfil extends AppCompatActivity {

    ImageView img;
    EditText ttnombre, ttapellido, ttpais,ttemail;
    private FirebaseAuth mAuth;
    ImageButton btn_cerrar;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        ttnombre = (EditText)findViewById(R.id.ttnombre);
        ttapellido = (EditText)findViewById(R.id.ttapellido);
        ttpais = (EditText)findViewById(R.id.ttpais);
        ttemail = (EditText)findViewById(R.id.ttemail);
        img = (ImageView)findViewById(R.id.img);

        btn_cerrar = (ImageButton)findViewById(R.id.btn_cerrar);

        //INICIALIZAR FIREBASE PARA OBTENER EL USUARIO ACTUAL
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        ttnombre.setText(currentUser.getDisplayName());
        ttemail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(img);


        btn_cerrar.setOnClickListener(new View.OnClickListener() {
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
                            Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(mainActivity);
                            ActivityPerfil.this.finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

}
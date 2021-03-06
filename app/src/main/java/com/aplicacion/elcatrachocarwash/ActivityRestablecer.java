package com.aplicacion.elcatrachocarwash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityRestablecer extends AppCompatActivity {

    AwesomeValidation awesomenValitation;
    private FirebaseAuth mAuth;
    EditText ttEmailRestablecer;
    Button boton_restablecer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer);

        ttEmailRestablecer = (EditText)findViewById(R.id.ttEmailRestablecer);
        boton_restablecer = (Button)findViewById(R.id.boton_restablecer);

        mAuth = FirebaseAuth.getInstance();
        awesomenValitation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomenValitation.addValidation(this,R.id.ttEmailRestablecer, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);

        boton_restablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerContra();
            }
        });

    }

    private void restablecerContra() {

        if(TextUtils.isEmpty(ttEmailRestablecer.getText())){
            Toast.makeText(this, "Ingresa una direccion de correo electronico valido", Toast.LENGTH_LONG).show();
        }else{
            String emailAddress = ttEmailRestablecer.getText().toString();

            if(awesomenValitation.validate()){
                mAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Hemos enviado un correo para restablecer su contrase??a", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
            }

        }


    }

}
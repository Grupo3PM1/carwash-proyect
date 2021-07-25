package com.aplicacion.elcatrachocarwash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import org.jetbrains.annotations.NotNull;

public class RegistrarUsuario extends AppCompatActivity {

    EditText tt_nombre, tt_apellido, tt_email, tt_contra, tt_pais;
    Button btn_registrar;
    private FirebaseAuth mAuth;
    AwesomeValidation awesomenValitation;
    TextView tt_sign2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        mAuth = FirebaseAuth.getInstance();
        awesomenValitation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomenValitation.addValidation(this,R.id.tt_email, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        awesomenValitation.addValidation(this,R.id.tt_contra, ".{6,}",R.string.invalid_password);


        tt_nombre = (EditText) findViewById(R.id.tt_nombre);
        tt_apellido = (EditText) findViewById(R.id.tt_apellido);
        tt_email = (EditText) findViewById(R.id.tt_email);
        tt_contra = (EditText) findViewById(R.id.tt_contra);
        btn_registrar = (Button)findViewById(R.id.btn_iniciar);
        tt_sign2 = (TextView)findViewById(R.id.tt_sign2);

        tt_sign2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correo = tt_email.getText().toString();
                String contra = tt_contra.getText().toString();

                if(awesomenValitation.validate()){
                    mAuth.createUserWithEmailAndPassword(correo,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistrarUsuario.this,"TU CUENTA HA SIDO CREADA",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }

                        }
                    });
                }else{
                    Toast.makeText(RegistrarUsuario.this, "INGRESE LOS DATOS", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(RegistrarUsuario.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(RegistrarUsuario.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(RegistrarUsuario.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(RegistrarUsuario.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                tt_email.setError("La dirección de correo electrónico está mal formateada.");
                tt_email.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(RegistrarUsuario.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                tt_contra.setError("la contraseña es incorrecta ");
                tt_contra.requestFocus();
                tt_contra.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(RegistrarUsuario.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(RegistrarUsuario.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(RegistrarUsuario.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(RegistrarUsuario.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                tt_email.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                tt_email.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(RegistrarUsuario.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(RegistrarUsuario.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(RegistrarUsuario.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(RegistrarUsuario.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(RegistrarUsuario.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(RegistrarUsuario.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(RegistrarUsuario.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                tt_contra.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                tt_contra.requestFocus();
                break;

        }

    }

}
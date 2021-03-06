package com.aplicacion.elcatrachocarwash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;


import com.aplicacion.elcatrachocarwash.ui.configuracionPerfil.PerfilUsuarioFragment;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    //VARIABLES GLOBALES//
    private FirebaseAuth mAuth;
    TextView tt_sign, tt_restablecercontra;
    EditText txtEmail, txtPass;
    AwesomeValidation awesomenValitation;
    Button btn_iniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            ///Inicializamos Firebase para conectarnos a una instancia existente
            mAuth = FirebaseAuth.getInstance();

            ///Validamos que el formato de correo y contrase??a esten correctos
            awesomenValitation = new AwesomeValidation(ValidationStyle.BASIC);
            awesomenValitation.addValidation(this,R.id.txtEmail, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
            awesomenValitation.addValidation(this,R.id.txtPass, ".{6,}",R.string.invalid_password);


            btn_iniciar = (Button)findViewById(R.id.btn_iniciar);
            txtEmail = (EditText)findViewById(R.id.txtEmail);
            txtPass = (EditText)findViewById(R.id.txtPass);
            tt_sign = (TextView)findViewById(R.id.tt_sign);
            tt_restablecercontra = (TextView)findViewById(R.id.tt_restablecercontra);

            //-------- INICIO DE EVENTO ONCLICK CON LOS BOTONES --------///

            //Crear una cuenta nueva, nos envia al Activity de RegistrarUsuario//
            tt_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegistrarUsuario.class);
                    startActivity(intent);
                }
            });


            //Restablecer contrase??a, nos envia al Activity de ActivityRestablecer//
            tt_restablecercontra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ActivityRestablecer.class);
                    startActivity(intent);

                }
            });


            //Acceso con Correo Electronico Verificado//
            btn_iniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = txtEmail.getText().toString();
                    String pass = txtPass.getText().toString();

                    if(awesomenValitation.validate()){
                        /// El correo electronico y la contrase??a que el usuario ingresen lo pasamos a signInWithEmailAndPassword
                        ///una vez que el correo electronico este verificado (ESTO PUEDE VERLO MEJOR EN EL ACTIVITY DE REGISTRAR USUARIO)
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                ///Task<AuthResult> devuelve un AuthResult, almacenado en un task cuando el objeto tenga exito
                                if(task.isSuccessful()){
                                    //si el task es correcto, es decir, si evalua que el correo ya esta verificado por el usuario
                                    //Creamos una variable de tipo FirebaseUser que llamaremos user que obtendra el usuario cuya sesion este activa, lo hacemos con el getCurrentUser()
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //luego verificamos el correo del usuario con isEmailVerified
                                    // Devuelve true si se verifica el correo electr??nico del usuario.
                                    if(!user.isEmailVerified()){
                                        ///si user devuelve un valor false, enviamos un mensaje al usuario con un Toast
                                        //donde le coomunicamos que todavia no ha ido a verificar su correo con el enlace que le enviamos
                                        clean();
                                        Toast.makeText(LoginActivity.this, "Correo electronico no verificado", Toast.LENGTH_LONG).show();
                                    }else{
                                        // si User nos devuelve un valor true significa que el correo esta verificado
                                        // Accede a la aplicacion
                                        Intent dashboardActivity = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(dashboardActivity);
                                    }

                                }
                                ///Si al ingresar el correo y la contrase??a, el usuario no puede acceder, esto puede significar varios problemas
                                //Puede ser que el correo no este verificado, o que el correo haya sido modificado o ya no exista
                                //por lo que hemos creado un metodo llamado dameToastdeerror(), donde se registran todos los posibles errores
                                else{
                                    String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                    dameToastdeerror(errorCode);
                                }
                            }
                        });
                    }else{
                        // Toast.makeText(LoginActivity.this,"Ingrese un correo y contrase??a", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }else{
            Toast.makeText(this,"Estas fuera de linea. Verifica tu conexion a internet y vuelve a intentarlo",Toast.LENGTH_SHORT).show();

        }



        //-------- FINAL DE EVENTO ONCLICK CON LOS BOTONES --------///

    }


    ///Limpiar los Editext
    private void clean() {
        txtEmail.setText("");
        txtPass.setText("");
    }

    ///Posibles probllemas si existe algun error en awesomenValitation
    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(LoginActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentaci??n", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(LoginActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(LoginActivity.this, "La credencial de autenticaci??n proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(LoginActivity.this, "La direcci??n de correo electr??nico est?? mal formateada.", Toast.LENGTH_LONG).show();
                txtEmail.setError("La direcci??n de correo electr??nico est?? mal formateada.");
                txtPass.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contrase??a no es v??lida o el usuario no tiene contrase??a.", Toast.LENGTH_LONG).show();
                txtPass.setError("la contrase??a es incorrecta ");
                txtPass.requestFocus();
                txtPass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(LoginActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inici?? sesi??n anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(LoginActivity.this,"Esta operaci??n es sensible y requiere autenticaci??n reciente. Inicie sesi??n nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(LoginActivity.this, "Ya existe una cuenta con la misma direcci??n de correo electr??nico pero diferentes credenciales de inicio de sesi??n. Inicie sesi??n con un proveedor asociado a esta direcci??n de correo electr??nico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(LoginActivity.this, "La direcci??n de correo electr??nico ya est?? siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                txtEmail.setError("La direcci??n de correo electr??nico ya est?? siendo utilizada por otra cuenta.");
                txtEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(LoginActivity.this, "Esta credencial ya est?? asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(LoginActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es v??lida. El usuario debe iniciar sesi??n nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(LoginActivity.this, "No hay ning??n registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es v??lida. El usuario debe iniciar sesi??n nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(LoginActivity.this, "Esta operaci??n no est?? permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contrase??a proporcionada no es v??lida..", Toast.LENGTH_LONG).show();
                txtPass.setError("La contrase??a no es v??lida, debe tener al menos 6 caracteres");
                txtPass.requestFocus();
                break;

        }


    }

    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){ //si no es null el usuario ya esta logueado
            //mover al usuario al dashboard
            if(user.isEmailVerified()){
                Intent dashboardActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(dashboardActivity);
            }
        }
        super.onStart();
    }


}
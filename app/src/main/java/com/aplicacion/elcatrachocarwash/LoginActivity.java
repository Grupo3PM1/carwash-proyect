package com.aplicacion.elcatrachocarwash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    //VARIABLES GLOBALES//
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    ImageButton btn_google;
    TextView tt_sign, tt_restablecercontra;
    EditText txtEmail, txtPass;
    AwesomeValidation awesomenValitation;
    Button btn_iniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configuramos el registro con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Creamos el inicio de sesion del usuario con las opciones especificadas por la variable gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ///Inicializamos Firebase para conectarnos a una instancia existente
        mAuth = FirebaseAuth.getInstance();

        ///Validamos que el formato de correo y contraseña esten correctos
        awesomenValitation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomenValitation.addValidation(this,R.id.txtEmail, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        awesomenValitation.addValidation(this,R.id.txtPass, ".{6,}",R.string.invalid_password);


        btn_google = (ImageButton) findViewById(R.id.btn_google);
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

        //Restablecer contraseña, nos envia al Activity de ActivityRestablecer//
        tt_restablecercontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ActivityRestablecer.class);
                startActivity(intent);
            }
        });

        //Acceso con Google, ejecuta el metodo SignIn//
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Acceso con Correo Electronico Verificado//
        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String email = txtEmail.getText().toString();
                    String pass = txtPass.getText().toString();

                    if(awesomenValitation.validate()){
                        /// El correo electronico y la contraseña que el usuario ingresen lo pasamos a signInWithEmailAndPassword
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
                                    // Devuelve true si se verifica el correo electrónico del usuario.
                                    if(!user.isEmailVerified()){
                                        ///si user devuelve un valor false, enviamos un mensaje al usuario con un Toast
                                        //donde le coomunicamos que todavia no ha ido a verificar su correo con el enlace que le enviamos
                                        clean();
                                        Toast.makeText(LoginActivity.this, "Correo electronico no verificado", Toast.LENGTH_LONG).show();
                                    }else{
                                        // si User nos devuelve un valor true significa que el correo esta verificado
                                        // Accede a la aplicacion
                                        ingresar();
                                    }

                                }
                                ///Si al ingresar el correo y la contraseña, el usuario no puede acceder, esto puede significar varios problemas
                                //Puede ser que el correo no este verificado, o que el correo haya sido modificado o ya no exista
                                //por lo que hemos creado un metodo llamado dameToastdeerror(), donde se registran todos los posibles errores
                                else{
                                    String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                    dameToastdeerror(errorCode);
                                }
                            }
                        });
                    }else{
                       // Toast.makeText(LoginActivity.this,"Ingrese un correo y contraseña", Toast.LENGTH_LONG).show();
                    }

            }
        });

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
                Toast.makeText(LoginActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(LoginActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(LoginActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(LoginActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                txtEmail.setError("La dirección de correo electrónico está mal formateada.");
                txtPass.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                txtPass.setError("la contraseña es incorrecta ");
                txtPass.requestFocus();
                txtPass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(LoginActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(LoginActivity.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(LoginActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(LoginActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                txtEmail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                txtEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(LoginActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(LoginActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(LoginActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(LoginActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(LoginActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(LoginActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                txtPass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                txtPass.requestFocus();
                break;

        }


    }


    //-------- MUERTE AL QUE TOQUE ESTOS METODOS -------///

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else {
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                                //Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                            Intent manActivity = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(manActivity);
                            LoginActivity.this.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void ingresar() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){ //si no es null el usuario ya esta logueado
            //mover al usuario al dashboard
            Intent dashboardActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(dashboardActivity);
        }

    }


    //-------- MUERTE AL QUE TOQUE ESTOS METODOS--------///



}
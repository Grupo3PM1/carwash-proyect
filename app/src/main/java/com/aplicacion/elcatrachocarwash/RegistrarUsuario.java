package com.aplicacion.elcatrachocarwash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.aplicacion.elcatrachocarwash.ui.configuracionPerfil.PerfilUsuarioFragment;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RegistrarUsuario extends AppCompatActivity {

    EditText tt_nombreap, tt_apellidoap, tt_email, tt_contra, tt_confirmar;
    Button btn_registrar;
    private FirebaseAuth mAuth;
    AwesomeValidation awesomenValitation;
    TextView tt_sign2;
    String correo, contra;


    // Array de Paises en Spinner
    private ArrayList<String> Paises;
    ArrayAdapter adp;
    private Spinner sp_paisap;
    private static final String DEFAULT_LOCAL = "Honduras";
    private String elemento;
    private String uid; // UID del Usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        mAuth = FirebaseAuth.getInstance();
        awesomenValitation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomenValitation.addValidation(this, R.id.tt_nombreap, "[a-zA-Z\\s]+", R.string.invalid_name1);
        awesomenValitation.addValidation(this, R.id.tt_apellidoap, "[a-zA-Z\\s]+", R.string.invalid_name2);
        awesomenValitation.addValidation(this, R.id.tt_email, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomenValitation.addValidation(this, R.id.tt_contra, ".{6,}", R.string.invalid_password);
        awesomenValitation.addValidation(this, R.id.tt_confirmar, R.id.tt_contra,R.string.no_coinciden);


        tt_nombreap = (EditText) findViewById(R.id.tt_nombreap);
        tt_apellidoap = (EditText) findViewById(R.id.tt_apellidoap);
        tt_email = (EditText) findViewById(R.id.tt_email);
        tt_contra = (EditText) findViewById(R.id.tt_contra);
        tt_confirmar = (EditText) findViewById(R.id.tt_confirmar);
        btn_registrar = (Button) findViewById(R.id.btn_iniciar);
        tt_sign2 = (TextView) findViewById(R.id.tt_sign2);
        sp_paisap = (Spinner) findViewById(R.id.sp_paisap); //Elemento del Spinner de Paises

        String[] paises = getResources().getStringArray(R.array.countries);
        Paises = new ArrayList<>(Arrays.asList(paises));

        adp = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Paises);
        sp_paisap.setAdapter(adp);
        sp_paisap.setSelection(adp.getPosition(DEFAULT_LOCAL));

        sp_paisap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                elemento = (String) sp_paisap.getAdapter().getItem(position);   // El elemento seleccionado del Spinner
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                correo = tt_email.getText().toString();
                contra = tt_contra.getText().toString();

                if(awesomenValitation.validate()){
                    mAuth.createUserWithEmailAndPassword(correo,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                onCreateDialog();
                                InsertEmail();
                                clean();

                            }else{
                                String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }

                        }
                    });
                }
            }
        });
    }

    private void clean() {
        tt_nombreap.setText("");
        tt_apellidoap.setText("");
        tt_email.setText("");
        tt_contra.setText("");
        tt_confirmar.setText("");
        sp_paisap.setSelection(adp.getPosition(DEFAULT_LOCAL));
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

    private void GetUID() {

        mAuth = FirebaseAuth.getInstance(); // Iniciar Firebase
        FirebaseUser user = mAuth.getCurrentUser();  // Obtener Usuario Actual

        // Si usuario no existe
        try {
            if (user != null) {
                uid = user.getUid(); // Obtener el UID del Usuario Actual
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+ e, Toast.LENGTH_LONG).show();
        }
    }

    private void InsertEmail() {

        GetUID();   // Obtener funcion UID para almacenarlo
        String url = RestApiMethod.ApiPostClientUrl;    // URL del RestAPI

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error en Response", "onResponse: " + error.getMessage().toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> parametros = new HashMap<String, String>();
                parametros.put("uid", uid);
                parametros.put("correo", tt_email.getText().toString());
                parametros.put("nombre", tt_nombreap.getText().toString()+" "+tt_apellidoap.getText().toString());
                parametros.put("pais", elemento);
                return parametros;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


       private void onCreateDialog() {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarUsuario.this);
            builder.setMessage("Hemos enviado un enlace de verificacion a "+correo+". Por favor revise su bandeja de entrada y carpeta de correo no deseado");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   finish();
               }
           });
            // Create the AlertDialog object and return it
            AlertDialog titulo =builder.create();
            titulo.show();


    }

}
package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.MockView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.elcatrachocarwash.MainActivity;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.RegistrarUsuario;
import com.aplicacion.elcatrachocarwash.RestApiMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PerfilUsuarioFragment extends Fragment {


     TextView ttnombre, ttemail, ttpais;
     private FirebaseAuth mAuth;
     ImageView img;
     ImageButton btn_galeria,btn_camara;
     Button btn_actualizar;
     byte [] Foto;
    View v;
    MotionEvent event;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_CAMARA = 100;


    private static Locale FilenameUtils;

    private String uid; // UID del Usuario

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            ttnombre = (TextView) view.findViewById(R.id.ttnombre);
            ttpais = (TextView) view.findViewById(R.id.ttpais);
            ttemail = (TextView) view.findViewById(R.id.ttemail);
            img = (ImageView) view.findViewById(R.id.img);
            btn_galeria = (ImageButton) view.findViewById(R.id.btn_galeria);
            btn_camara = (ImageButton) view.findViewById(R.id.btn_camara);
            btn_actualizar = (Button) view.findViewById(R.id.btn_actualizar);


            ttpais.setEnabled(false);
            ttemail.setEnabled(false);

            btn_galeria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent, "Seleccione la aplicacion"), 10);


                }
            });

            btn_camara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    permisos();


                }
            });


            btn_actualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actualizarPersona();
                    //createNotification();
                }
            });


            GetUser();  ///cargar usuario


        }else{
            Toast.makeText(getActivity(),"Sin conexion a internet",Toast.LENGTH_SHORT).show();
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Se encuentra fuera de linea, verifique su conexion a internet y vuelva a intentar.");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            });
                            // Create the AlertDialog object and return it
                            AlertDialog titulo =builder.create();
                            titulo.show();

                            break;
                    }
                    return false;
                }
            });
        }

        return view;

    }






        /*
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        ttnombre.setText(currentUser.getDisplayName());
        ttemail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(img);
        */








    private void actualizarusuario() {


    }


    private void GetUser() {

        mAuth = FirebaseAuth.getInstance(); // Iniciar Firebase
        FirebaseUser user = mAuth.getCurrentUser();  // Obtener Usuario Actual

        // Si usuario no existe
        try {
            if (user != null) {
                uid = user.getUid(); // Obtener el UID del Usuario Actual
                SearchUID("https://dandsol.000webhostapp.com/ElCatrachoCarwash/buscar_cliente.php?uid="+uid+"");
            }
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Error: "+ e, Toast.LENGTH_LONG).show();
        }
    }

    private void SearchUID(String URL) {
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        ttnombre.setText(jsonObject.getString("clnt_nombre"));
                        ttpais.setText(jsonObject.getString("clnt_pais"));
                        ttemail.setText(jsonObject.getString("clnt_email"));
                        //Traemos foto tipo Blob de BD como String Base64 y decodificamos a ByteArray
                        byte[] decodedString = Base64.decode(jsonObject.getString("clnt_foto"), 0);
                        //de ByteArray lo convertimos a Bitmap
                        Bitmap bitmapfotoBD = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        //Seteamos dicho Bitmap en el ImageView
                        img.setImageBitmap(bitmapfotoBD);
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(this.getActivity());
        requestQueue.add(jsonArrayRequest);
    }



                                  //FOTOGRAFIA DESDE GALERIA Y CAMARA//


    private void permisos()
    {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED  &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                                                    PETICION_ACCESO_CAMARA);
        }
        else
        {

            dispatchTakePictureIntent1();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESO_CAMARA) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent1();

            }
        }
        else {
            Toast.makeText(getContext(), "Se necesitan permisos de acceso", Toast.LENGTH_LONG).show();
        }
    }


    private void dispatchTakePictureIntent1() {//Tomar fotografia
        Intent takePictureIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent1.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent1, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { //Mostrar desde galeria
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }

       else if (resultCode == getActivity().RESULT_OK) {
            Uri path=data.getData();
            img.setImageURI(path);

        }

    }


                        //METODO ACTUALIZAR NOMBRE Y/O FOTOGRAFIA//
    private void actualizarPersona(){
        String URL = RestApiMethod.ApiPutUrlClient;
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "Operacion Exitosa", Toast.LENGTH_SHORT).show();
                /*Toast.makeText(getContext(), GetStringImage(img), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), ttnombre.getText().toString(), Toast.LENGTH_SHORT).show();*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("uid",uid);
                parametros.put("nombre",ttnombre.getText().toString());
                parametros.put("foto",GetStringImage(img));
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
    //FIN METODO ACTUALIZAR NOMBRE Y/O FOTOGRAFIA//


    public static String GetStringImage(ImageView img)
    {

        //Traemos la Imagen del Imageview y construimos su dibujo cache
        img.buildDrawingCache();
        //Extraemos ese dibujo cache y lo guardamos en un Bitmap
        Bitmap bitmap = img.getDrawingCache();
        //Iniciamos una Salida de Stream para ByteArray
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Comprimimos dicho Bitmap en el Stream para ByteArray como JPEG
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //Guardamos ese Strean para ByteArray en un propio ByteArray
        byte[] imagebyte = stream.toByteArray();
        //Creamos un String donde codificamos el ByteArray a Base64
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        //Retornamos dicho String con Base64 del ByteArray
        return encode;

    }




}
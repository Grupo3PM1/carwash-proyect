package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.solver.ArrayRow;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.elcatrachocarwash.LoginActivity;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.RestApiMethod;
import com.aplicacion.elcatrachocarwash.ui.clases.Spinners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PerfilVehiculoFragment extends Fragment {

    private AsyncHttpClient http;
    private FirebaseAuth mAuth;     // Iniciar Firebase
    private String uid;             // UID del Usuario en Firebase
    private String idUser;          // ID del Usuario en MySQL
    private String URLVehicle;      // URL de Spinner Vehiculo
    private String[] iddevehiculo= new String[900];
    private ArrayList<Spinners> lista;
    private String Id_Vehiculo, IdVehiculoBD;

    ArrayAdapter<Spinners> adp;
    ArrayList ArrayLista;

    ListView Lista;
    Button btnConfigurar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_vehiculo, container, false);

        Lista = (ListView)view.findViewById(R.id.lista);
        btnConfigurar = (Button)view.findViewById(R.id.btnConfigurar);
        http = new AsyncHttpClient();

        btnConfigurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "El Id Seleccionado es: "+IdVehiculoBD, Toast.LENGTH_SHORT).show();
                eliminarVehiculo();
            }
        });

        GetUser();

        final int interval = 1500; // 1 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            public void run() {
                ObtenerVehiculos();     // Funcion para cargar Vehiculos en Listview
                Lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                        //Id_Vehiculo = String.valueOf(position);
                        IdVehiculoBD = iddevehiculo[position];
                        //Toast.makeText(getContext(), "Id De este es: "+IdVehiculoBD, Toast.LENGTH_SHORT).show();

                    }
                });
            }
            };

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);

        // Inflate the layout for this fragment
        return view;
    }


    //-------- INICIO DE EVENTO DE BUSCAR ID DE USUARIO --------///

    private void GetUser() {

        mAuth = FirebaseAuth.getInstance();            // Iniciar Firebase
        FirebaseUser user = mAuth.getCurrentUser();     // Obtener Usuario Actual

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
                        idUser = jsonObject.getString("clnt_id");
                        URLVehicle = "https://dandsol.000webhostapp.com/ElCatrachoCarwash/buscar_vehiculo_lista.php?id="+idUser+"";
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

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    //-------- FINAL DE EVENTO DE BUSCAR ID DE USUARIO --------///

    //-------- INICIO DE EVENTO DE LISTVIEW CON MYSQL --------///

    // Obtener Vehiculos de Usuario con MySQL
    public void ObtenerVehiculos() {

        http.post(URLVehicle, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaVehiculos(new String (responseBody));
                    ListarIDVehiculos(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaVehiculos(String URL){
        lista = new ArrayList<Spinners>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Spinners m = new Spinners();
                m.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                lista.add(m);
            }

            adp = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, lista);
            Lista.setAdapter(adp);
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private void ListarIDVehiculos(String URL){
        try {

            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                iddevehiculo[i] = jsonArreglo.getJSONObject(i).getString("idVehiculo");
                //Toast.makeText(getContext(), "Aqui: "+iddevehiculo[i], Toast.LENGTH_SHORT).show();
            }

            /*
            Toast.makeText(getContext(), "Primer: "+IDVEHICULO[0], Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Segundo: "+IDVEHICULO[1], Toast.LENGTH_SHORT).show();*/
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    //-------- FINAL DE EVENTO DE LISTVIEW CON MYSQL --------///


    //                     METODO ELIMINAR VEHICULO                      //

    private void eliminarVehiculo(){
        String URL = RestApiMethod.ApiDeleteVehicleUrl;
        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "Operacion Exitosa", Toast.LENGTH_SHORT).show();
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
                parametros.put("id", IdVehiculoBD);
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }
    //           FIN METODO ELIMINAR VEHICULO             //
}
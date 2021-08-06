package com.aplicacion.elcatrachocarwash.ui.cotizacion;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.RestApiMethod;
import com.aplicacion.elcatrachocarwash.ui.clases.Aceites;
import com.aplicacion.elcatrachocarwash.ui.clases.Modelos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class Fragment_Registro_auto extends Fragment {

    private AsyncHttpClient http;
    private FirebaseAuth mAuth;     // Iniciar Firebase
    private String uid;             // UID del Usuario en Firebase

    private String idUser;          // ID del Usuario en MySQL

    // Variables de elementos Spinner
    private Spinner sp_marca, sp_aceite, sp_anio;
    private String ItemMarcaModelo, ItemAnio, ItemTAceite;

    // Variables de Botones y eventos
    private Button btn_guardar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_vehiculo, container, false);

        http = new AsyncHttpClient();

        // Elementos del diseño de Spinners
        sp_marca = (Spinner)view.findViewById(R.id.spmarca); //Elemento del Spinner de Marca y modelo
        sp_anio = (Spinner)view.findViewById(R.id.spanio); //Elemento del Spinner de Años
        sp_aceite = (Spinner)view.findViewById(R.id.sptipoaceite); //Elemento del Spinner de Tipo de Aceite

        btn_guardar = (Button)view.findViewById(R.id.btnConfigurar);

        ObtenerModelos();   // Lista de Modelos en Spinner Marca y modelo
        ObtenerAnio();      // Lista de Año en Spinner Años
        ObtenerAceites();   // Lista de Aceites en Spinner Tipo de Aceite

        GetUser();          // Obtener ID del usuario en MySQL

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertVehicle();    // Insertar en MySQL datos de Vehiculo del Usuario
                CleanScreen();
            }
        });

        return view;
    }

    //-------- INICIO DE EVENTO DE SPINNER CON MYSQL --------///

    // Obtener Modelos y Marcas con MySQL
    public void ObtenerModelos() {
        String URL = RestApiMethod.ApiGetModels;    // URL de recurso PHP

        http.post(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaModelos(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaModelos(String URL){
        ArrayList<Modelos> lista = new ArrayList<Modelos>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Modelos m = new Modelos();
                m.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                lista.add(m);
            }

            ArrayAdapter<Modelos> adp = new ArrayAdapter<Modelos>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista);
            sp_marca.setAdapter(adp);

            sp_marca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ItemMarcaModelo = (String) sp_marca.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    // Obtener Años
    public void ObtenerAnio() {

        try {
            String[] anio = new String[32];
            ArrayList<String> Anios;

            for(int i=0; i<32; i++){
                anio[i] = (i+1990)+"";
            }

            Anios = new ArrayList<>(Arrays.asList(anio));
            ArrayAdapter adp = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, Anios);
            sp_anio.setAdapter(adp);

            sp_anio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ItemAnio = (String) sp_anio.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    // Obtener Tipos de aceite con MySQL
    public void ObtenerAceites() {
        String URL = RestApiMethod.ApiGetOil;   // URL de recurso PHP

        http.post(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaAceites(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaAceites(String URL){
        ArrayList<Aceites> lista = new ArrayList<Aceites>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Aceites a = new Aceites();
                a.setNombre(jsonArreglo.getJSONObject(i).getString("tpact_nombre"));
                lista.add(a);
            }

            ArrayAdapter<Aceites> adp = new ArrayAdapter<Aceites>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista);
            sp_aceite.setAdapter(adp);

            sp_aceite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ItemTAceite = (String) sp_aceite.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    //-------- FINAL DE EVENTO DE SPINNER CON MYSQL --------///

    //-------- INICIO DE EVENTO DE ALMACENAR DATOS DE VEHICULO CON MYSQL --------///

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

    private void InsertVehicle() {

        String url = RestApiMethod.ApiPostVehicleUrl;    // URL del RestAPI

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
                parametros.put("marcamodelo", ItemMarcaModelo);
                parametros.put("anio", ItemAnio);
                parametros.put("taceite", ItemTAceite);
                parametros.put("idUser", idUser);
                return parametros;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    //-------- FINAL DE EVENTO DE ALMACENAR DATOS DE VEHICULO CON MYSQL --------///

    //-------- INICIO DE EVENTO DE REINICIAR FRAGMENT --------///

    private void CleanScreen() {
        Fragment frg = null;
        frg = getActivity().getSupportFragmentManager().findFragmentByTag("Fragment_Registro_auto.class");
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    //-------- FINAL DE EVENTO DE REINICIAR FRAGMENT --------///
}
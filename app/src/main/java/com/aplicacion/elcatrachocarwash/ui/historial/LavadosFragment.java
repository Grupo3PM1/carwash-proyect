package com.aplicacion.elcatrachocarwash.ui.historial;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.ui.clases.Historial;
import com.aplicacion.elcatrachocarwash.ui.clases.Spinners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LavadosFragment extends Fragment {

    private AsyncHttpClient http;
    private FirebaseAuth mAuth;     // Iniciar Firebase
    private String uid;             // UID del Usuario en Firebase
    private String idUser;          // ID del Usuario en MySQL
    private String URLQuotation;      // URL de Lista Cotizacion Vehiculo

    RecyclerView recycler;

    ArrayList<Historial> historials;
    ArrayList<Historial> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lavados, container, false);

        http = new AsyncHttpClient();

        items = new ArrayList<>();

        // Obtener el Recycler
        recycler = (RecyclerView) view.findViewById(R.id.reciclador);

        // Usar un administrador para LinearLayout
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        GetUser();

        final int interval = 2000; // 1 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            public void run() {
                // Inicializar Cotizaciones
                ObtenerCotizacion();
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
                        URLQuotation = "https://dandsol.000webhostapp.com/ElCatrachoCarwash/ver_historial_lavados.php?id="+idUser+"";
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

    //-------- INICIO DE EVENTO DE RECYCLERVIEW CON MYSQL --------///

    // Obtener Cotizaciones de Usuario con MySQL
    public void ObtenerCotizacion() {

        http.post(URLQuotation, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaCotizacion(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaCotizacion(String URL){
        historials = new ArrayList<Historial>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Historial h = new Historial();
                h.setNumero(jsonArreglo.getJSONObject(i).getString("numero"));
                h.setVehiculo(jsonArreglo.getJSONObject(i).getString("vehiculo"));
                h.setServicio(jsonArreglo.getJSONObject(i).getString("servicio"));
                h.setUbicacion(jsonArreglo.getJSONObject(i).getString("ubicacion"));
                h.setFecha(jsonArreglo.getJSONObject(i).getString("fecha"));
                h.setEstado(jsonArreglo.getJSONObject(i).getString("estado"));
                historials.add(h);
            }

            HistorialList();

            // Crear un nuevo adaptador
            HistorialAdapter adapter = new HistorialAdapter(items);
            recycler.setAdapter(adapter);
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private void HistorialList() {

        for (int i = 0;  i < historials.size(); i++){
            items.add(new Historial(
                    historials.get(i).getNumero(),
                    historials.get(i).getVehiculo(),
                    historials.get(i).getServicio(),
                    historials.get(i).getUbicacion(),
                    historials.get(i).getFecha(),
                    historials.get(i).getEstado()));
        }
    }

    //-------- FINAL DE EVENTO DE RECYCLERVIEW CON MYSQL --------///
}
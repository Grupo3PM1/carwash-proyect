package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.RestApiMethod;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import com.aplicacion.elcatrachocarwash.ui.clases.Modelos;
import com.aplicacion.elcatrachocarwash.ui.clases.Aceites;
import com.loopj.android.http.Base64;

import cz.msebera.android.httpclient.Header;

public class PerfilVehiculoFragment extends Fragment {

    private AsyncHttpClient http;

    // Array de Paises en Spinner
    private Spinner sp_marca, sp_aceite, sp_anio;
    private String elemento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_vehiculo, container, false);

        http = new AsyncHttpClient();

        // Elementos del diseño de Spinners
        sp_marca = (Spinner)view.findViewById(R.id.spmarca); //Elemento del Spinner de Paises
        sp_aceite = (Spinner)view.findViewById(R.id.sptipoaceite); //Elemento del Spinner de Paises
        sp_anio = (Spinner)view.findViewById(R.id.spanio); //Elemento del Spinner de Paises

        ObtenerModelos();
        ObtenerAnio();
        ObtenerAceites();

        // Inflate the layout for this fragment
        return view;
    }

    public void ObtenerModelos() {
        String URL = RestApiMethod.ApiGetModels;

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
                    elemento = (String) sp_marca.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner
                    Toast.makeText(getContext(), "" + elemento, Toast.LENGTH_LONG).show();
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
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    public void ObtenerAceites() {
        String URL = RestApiMethod.ApiGetOil;

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
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }
}
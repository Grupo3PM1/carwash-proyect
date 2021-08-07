package com.aplicacion.elcatrachocarwash.ui.cotizacion;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.elcatrachocarwash.MapsActivity;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.RestApiMethod;
import com.aplicacion.elcatrachocarwash.databinding.FragmentCotizacionBinding;
import com.aplicacion.elcatrachocarwash.ui.clases.Spinners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


public class CotizacionFragment extends Fragment implements View.OnClickListener {


    private com.aplicacion.elcatrachocarwash.ui.cotizacion.CotizacionViewModel cotizacionViewModel;
    private FragmentCotizacionBinding binding;

    private AsyncHttpClient http;
    private FirebaseAuth mAuth;     // Iniciar Firebase
    private String uid;             // UID del Usuario en Firebase
    private String idUser;          // ID del Usuario en MySQL
    private String URLVehicle;      // URL de Spinner Vehiculo

    Button btnfecha, btnhora, btncotizacion;
    EditText txtfecha, txthora;

    // Variables de elementos Spinner de Registro de Vehículo
    private Spinner spvehiculo, spservicio, spubicacion;
    private String ItemVehiculo, ItemServicio, ItemUbiacion;

    // Variables de Botones y eventos de Registro de Vehículo
    private Button btn_guardar;

    private int dia, mes, anio, hora, minutos;
    private int seleccionar;

    private String[] arraycontenido2;

    private ArrayAdapter adapter2;
    private boolean isFirstTime = true;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        cotizacionViewModel =
                new ViewModelProvider(this).get(com.aplicacion.elcatrachocarwash.ui.cotizacion.CotizacionViewModel.class);

        View view = inflater.inflate(R.layout.fragment_cotizacion, container, false);

        http = new AsyncHttpClient();

        spvehiculo = (Spinner)view.findViewById(R.id.spvehiculo);
        spservicio = (Spinner)view.findViewById(R.id.spservicio);
        spubicacion = (Spinner)view.findViewById(R.id.spubicacion);
        btnfecha = (Button)view.findViewById(R.id.btnfecha);
        btnhora = (Button)view.findViewById(R.id.btnhora);
        txtfecha = (EditText)view.findViewById(R.id.txtfecha);
        txthora = (EditText)view.findViewById(R.id.txthora);
        btn_guardar = (Button)view.findViewById(R.id.btncotizacion);

        btnfecha.setOnClickListener(this);
        btnhora.setOnClickListener(this);


        txtfecha.setEnabled(false);
        txthora.setEnabled(false);

        GetUser();

        final int interval = 2000; // 1 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            public void run() {
                ObtenerVehiculos();
                ObtenerServicios();
            }
        };

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);



        //        SELECION DEL TIPO DE SERVICIO             //
        spubicacion = (Spinner)view.findViewById(R.id.spubicacion);
        arraycontenido2 = new String[]{"Seleccione","Centro de Servicio", "A Domicilio"};
        ArrayList<String> ubicacion = new ArrayList<>(Arrays.asList(arraycontenido2));
        adapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, ubicacion);
        spubicacion.setAdapter(adapter2);
        spubicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstTime){
                    isFirstTime = false;
                }

                if (arraycontenido2[position] == "A Domicilio"){
                    seleccionar=0;

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("decision", seleccionar);
                    startActivity(intent);
                }
                else if(arraycontenido2[position]== "Centro de Servicio"){
                    seleccionar=1;

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("decision", seleccionar);
                    startActivity(intent);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //        FIN DE SELECION DEL TIPO DE SERVICIO             //

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
                        URLVehicle = "https://dandsol.000webhostapp.com/ElCatrachoCarwash/buscar_vehiculo_cliente.php?id="+idUser+"";
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

    //-------- INICIO DE EVENTO DE SPINNER CON MYSQL --------///

    // Obtener Vehiculos de Usuario con MySQL
    public void ObtenerVehiculos() {

        http.post(URLVehicle, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaVehiculos(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaVehiculos(String URL){
        ArrayList<Spinners> lista = new ArrayList<Spinners>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Spinners m = new Spinners();
                m.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                lista.add(m);
            }

            ArrayAdapter<Spinners> adp = new ArrayAdapter<Spinners>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista);
            spvehiculo.setAdapter(adp);

            spvehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ItemVehiculo = (String) spvehiculo.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner
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

    // Obtener Servicios con MySQL
    public void ObtenerServicios() {
        String URL = RestApiMethod.ApiGetServices;   // URL de recurso PHP

        http.post(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    ListaServicios(new String (responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ListaServicios(String URL){
        ArrayList<Spinners> lista = new ArrayList<Spinners>();
        try {
            JSONArray jsonArreglo = new JSONArray(URL);
            for(int i=0; i<jsonArreglo.length(); i++){
                Spinners a = new Spinners();
                a.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                lista.add(a);
            }

            ArrayAdapter<Spinners> adp = new ArrayAdapter<Spinners>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista);
            spservicio.setAdapter(adp);

            spservicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ItemServicio = (String) spservicio.getAdapter().getItem(position).toString();   // El elemento seleccionado del Spinner

                    String CA = "Cambio de Aceite (Depende del vehiculo)";

                    if (ItemServicio.equals(CA)) {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setMessage("Unicamente se hace en centro de servicio")
                                .setCancelable(false)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        spubicacion.setSelection(adapter2.getPosition("Centro de Servicio"));
                                        spubicacion.setEnabled(false);
                                    }
                                });
                        AlertDialog titulo = alerta.create();
                        titulo.setTitle("Aviso");
                        titulo.show();

                    }
                    else{
                        spubicacion.setEnabled(true);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        //-------- FINAL DE EVENTO DE SPINNER CON MYSQL --------///
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //--------         SELECCION DE FECHA Y HORA        --------//
    @Override
    public void onClick(View v) {

        if (v == btnfecha){


            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            anio = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    txtfecha.setText(dayOfMonth+"/"+(dayOfMonth+1)+"/"+year);
                }
            },anio,mes,dia);
            datePickerDialog.show();


        }if (v == btnhora){
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    txthora.setText(hourOfDay+":"+minute);
                }
            }, hora,minutos,false);
            timePickerDialog.show();
        }
    }
    //--------         FIN DE SELECCION DE FECHA Y HORA        --------///
}
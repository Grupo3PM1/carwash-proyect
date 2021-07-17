package com.aplicacion.elcatrachocarwash.ui.cotizacion;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aplicacion.elcatrachocarwash.MapsActivity;
import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.databinding.FragmentCotizacionBinding;

import java.util.Calendar;

public class CotizacionFragment extends Fragment implements View.OnClickListener {

    private com.aplicacion.elcatrachocarwash.ui.cotizacion.CotizacionViewModel cotizacionViewModel;
    private FragmentCotizacionBinding binding;

    Button btnfecha, btnhora, btncotizacion;
    EditText txtfecha, txthora;
    Spinner spvehiculo, spservicio, spubicacion;

    private int dia, mes, anio, hora, minutos;

    private Spinner spinner;
    private String[] arraycontenido;
    private com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner adapter;

    private Spinner spinner1;
    private String[] arraycontenido1;
    private com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner adapter1;

    private Spinner spinner2;
    private String[] arraycontenido2;
    private com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner adapter2;

    private boolean isFirstTime = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        cotizacionViewModel =
                new ViewModelProvider(this).get(com.aplicacion.elcatrachocarwash.ui.cotizacion.CotizacionViewModel.class);

        View view = inflater.inflate(R.layout.fragment_cotizacion, container, false);
        spinner = (Spinner)view.findViewById(R.id.spvehiculo);
        arraycontenido = new String[]{"Honda-Civic", "KIA-Sorento", "Mazda-CX5", "Mercedes-Vito"};
        adapter = new com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner(getActivity(), arraycontenido);
        spinner.setAdapter(adapter);

        spinner1 = (Spinner)view.findViewById(R.id.spservicio);
        arraycontenido1 = new String[]{"Lavado General", "Lavado Completo", "Lavado de Motor", "Cambio de aceite"};
        adapter1 = new com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner(getActivity(), arraycontenido1);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstTime){
                    isFirstTime = false;
                }
                if (arraycontenido1[position] == "Cambio de aceite") {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setMessage("Unicamente se hace en centro de servicio")
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog titulo = alerta.create();
                        titulo.setTitle("Aviso");
                        titulo.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2 = (Spinner)view.findViewById(R.id.spubicacion);
        arraycontenido2 = new String[]{"Centro de Servicio", "A Domicilio"};
        adapter2 = new com.aplicacion.elcatrachocarwash.ui.cotizacion.AdaptadorSpinner(getActivity(), arraycontenido2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstTime){
                    isFirstTime = false;
                }
                if (arraycontenido2[position] == "A Domicilio"){
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spvehiculo = (Spinner)view.findViewById(R.id.spvehiculo);
        spservicio = (Spinner)view.findViewById(R.id.spservicio);
        spubicacion = (Spinner)view.findViewById(R.id.spubicacion);
        btnfecha = (Button)view.findViewById(R.id.btnfecha);
        btnhora = (Button)view.findViewById(R.id.btnhora);
        txtfecha = (EditText)view.findViewById(R.id.txtfecha);
        txthora = (EditText)view.findViewById(R.id.txthora);

        btnfecha.setOnClickListener(this);
        btnhora.setOnClickListener(this);

        /*binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome1;
        cotizacionViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v == btnfecha){
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            anio = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    txtfecha.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },dia,mes,anio);
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
}
package com.aplicacion.elcatrachocarwash.ui.configuracionPerfil;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aplicacion.elcatrachocarwash.R;

public class PerfilVehiculoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_vehiculo, container, false);

        // Inflate the layout for this fragment
        return view;
    }
}
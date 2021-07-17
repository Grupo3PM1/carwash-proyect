package com.aplicacion.elcatrachocarwash.ui.cotizacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CotizacionViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CotizacionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Seleccione un vehiculo");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
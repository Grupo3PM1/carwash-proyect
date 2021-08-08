package com.aplicacion.elcatrachocarwash.ui.historial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.elcatrachocarwash.R;
import com.aplicacion.elcatrachocarwash.ui.clases.Historial;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {
    private List<Historial> items;

    public HistorialAdapter(List<Historial> items) {
        this.items = items;
    }

    @Override
    public HistorialViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.historiales, null, false);
        return new HistorialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistorialViewHolder viewHolder, int i) {
        viewHolder.TVnumero.setText(items.get(i).getNumero());
        viewHolder.TVvehiculo.setText(items.get(i).getVehiculo());
        viewHolder.TVservicio.setText(items.get(i).getServicio());
        viewHolder.TVubicacion.setText(items.get(i).getUbicacion());
        viewHolder.TVfecha.setText(items.get(i).getFecha());
        viewHolder.TVestado.setText(items.get(i).getEstado());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView TVnumero, TVvehiculo, TVservicio, TVubicacion, TVfecha, TVestado;

        public HistorialViewHolder(View v) {
            super(v);
            TVnumero = (TextView) v.findViewById(R.id.txtnumero);
            TVvehiculo = (TextView) v.findViewById(R.id.txtvehiculo);
            TVservicio = (TextView) v.findViewById(R.id.txtservicio);
            TVubicacion = (TextView) v.findViewById(R.id.txtTipoUbicacion);
            TVfecha = (TextView) v.findViewById(R.id.txtfecha);
            TVestado = (TextView) v.findViewById(R.id.txtFechaEmision);
        }
    }
}

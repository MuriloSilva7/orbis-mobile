package com.orbis.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.orbis.mobile.R;
import com.orbis.mobile.model.Sensor;
import com.orbis.mobile.ui.main.SensorDetalheActivity;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<Sensor> listaSensores;
    private List<Sensor> listaOriginal;

    public SensorAdapter(List<Sensor> listaSensores){
        this.listaSensores = listaSensores;
        this.listaOriginal = new ArrayList<>(listaSensores);
    }

    public void atualizarLista(List<Sensor> novaLista) {
        this.listaSensores = novaLista;
        this.listaOriginal = new ArrayList<>(novaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        List<Sensor> filtrada = new ArrayList<>();
        if (texto.isEmpty()) {
            filtrada.addAll(listaOriginal);
        } else {
            String query = texto.toLowerCase().trim();
            for (Sensor item : listaOriginal) {
                if (item.getTipo().toLowerCase().contains(query)) {
                    filtrada.add(item);
                }
            }
        }
        this.listaSensores = filtrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorAdapter.SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        return new SensorAdapter.SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorAdapter.SensorViewHolder holder, int position) {
        Sensor sensor = listaSensores.get(position);
        holder.txtTipoVariavel.setText(sensor.getTipo());

        holder.btnVerMais.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SensorDetalheActivity.class);
            intent.putExtra("id_sensor", sensor.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaSensores != null ? listaSensores.size() : 0;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder{
        TextView txtTipoVariavel;
        MaterialButton btnVerMais;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTipoVariavel = itemView.findViewById(R.id.txtTipoVariavel);
            btnVerMais = (MaterialButton) itemView.findViewById(R.id.btnVerMais);
        }
    }
}

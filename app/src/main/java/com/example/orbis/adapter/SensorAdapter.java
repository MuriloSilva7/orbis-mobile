package com.example.orbis.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbis.R;
import com.example.orbis.model.Maquina;
import com.example.orbis.model.Sensor;
import com.example.orbis.ui.main.MaquinaDetalheActivity;
import com.example.orbis.ui.main.SensorDetalheActivity;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {


    private List<Sensor> listaSensores;

    public SensorAdapter(List<Sensor> listaSensores){
        this.listaSensores = listaSensores;
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
        return 0;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        TextView txtTipoVariavel;
        Button btnVerMais;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTipoVariavel = itemView.findViewById(R.id.txtTipoVariavel);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
        }
    }
}

package com.example.orbis.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbis.R;
import com.example.orbis.model.Alerta;
import com.example.orbis.ui.main.AlertaDetalheActivity;

import java.util.List;

public class AlertaAdapter extends RecyclerView.Adapter<AlertaAdapter.ViewHolder> {

    private List<Alerta> lista;

    public AlertaAdapter(List<Alerta> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeVariavel;
        Button btnVerMais;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alerta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alerta alerta = lista.get(position);

        if (alerta.getMaquina() != null) {
            holder.txtNomeVariavel.setText(alerta.getMaquina().getNome());
        } else {
            holder.txtNomeVariavel.setText("Máquina desconhecida");
        }

        holder.btnVerMais.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    AlertaDetalheActivity.class
            );

            intent.putExtra(
                    "id_alerta",
                    alerta.getId()
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
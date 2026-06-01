package com.orbis.mobile.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.ui.main.AlertaDetalheActivity;

import java.util.ArrayList;
import java.util.List;

public class AlertaAdapter extends RecyclerView.Adapter<AlertaAdapter.ViewHolder> {

    private List<Alerta> lista;
    private List<Alerta> listaOriginal;

    public AlertaAdapter(List<Alerta> lista) {
        this.lista = lista;
        this.listaOriginal = new ArrayList<>(lista);
    }

    public void atualizarLista(List<Alerta> novaLista) {
        this.lista = novaLista;
        this.listaOriginal = new ArrayList<>(novaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        List<Alerta> filtrada = new ArrayList<>();
        if (texto.isEmpty()) {
            filtrada.addAll(listaOriginal);
        } else {
            String query = texto.toLowerCase().trim();
            for (Alerta item : listaOriginal) {
                if (item.getMaquina() != null && item.getMaquina().getNome().toLowerCase().contains(query)) {
                    filtrada.add(item);
                } else if (item.getMensagem() != null && item.getMensagem().toLowerCase().contains(query)) {
                    filtrada.add(item);
                }
            }
        }
        this.lista = filtrada;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeVariavel;
        Button btnVerMais;
        ImageView imgAlerta;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
            imgAlerta = itemView.findViewById(R.id.imgAlerta);
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

            if (alerta.getMaquina().getImagem() != null && !alerta.getMaquina().getImagem().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(alerta.getMaquina().getImagem())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.imgAlerta);
            }
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
        return lista != null ? lista.size() : 0;
    }
}

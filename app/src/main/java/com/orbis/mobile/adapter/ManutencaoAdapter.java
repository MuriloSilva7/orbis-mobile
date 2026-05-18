package com.orbis.mobile.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.model.Manutencao;

import java.util.List;

public class ManutencaoAdapter extends RecyclerView.Adapter<ManutencaoAdapter.MyViewHolder> {

    private List<Manutencao> listaManutencoes;

    public ManutencaoAdapter(List<Manutencao> lista) {
        this.listaManutencoes = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manutencao, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Manutencao manutencao = listaManutencoes.get(position);

        if (manutencao.getAlerta() != null && manutencao.getAlerta().getMaquina() != null) {
            holder.txtMaquinaNome.setText(manutencao.getAlerta().getMaquina().getNome());
        } else {
            holder.txtMaquinaNome.setText("Manutenção #" + manutencao.getId());
        }

        if (manutencao.getUsuario() != null) {
            holder.txtTecnicoNome.setText("Técnico: " + manutencao.getUsuario().getNome());
        } else {
            holder.txtTecnicoNome.setText("Técnico: Não informado");
        }

        holder.txtData.setText("Data: " + manutencao.getCriadoEm());
        holder.txtObservacao.setText(manutencao.getObservacao() != null ? manutencao.getObservacao() : "Sem observações.");
        holder.txtStatus.setText(manutencao.getStatus());

        // Estilização do Status
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(16f);

        if ("RESOLVIDO".equals(manutencao.getStatus())) {
            holder.txtStatus.setText("CONCLUÍDO");
            drawable.setColor(Color.parseColor("#22C55E")); // statusGreen
        } else if ("EM_ANDAMENTO".equals(manutencao.getStatus())) {
            holder.txtStatus.setText("EM ANDAMENTO");
            drawable.setColor(Color.parseColor("#F59E0B")); // statusOrange
        } else {
            drawable.setColor(Color.parseColor("#808080")); // gray
        }
        holder.txtStatus.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return listaManutencoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaquinaNome, txtTecnicoNome, txtData, txtStatus, txtObservacao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaquinaNome = itemView.findViewById(R.id.txtMaquinaNome);
            txtTecnicoNome = itemView.findViewById(R.id.txtTecnicoNome);
            txtData = itemView.findViewById(R.id.txtData);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtObservacao = itemView.findViewById(R.id.txtObservacao);
        }
    }
}

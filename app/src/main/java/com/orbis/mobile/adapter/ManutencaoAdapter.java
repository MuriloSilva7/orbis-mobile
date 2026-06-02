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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        holder.txtData.setText("Data: " + formatarData(manutencao.getCriadoEm()));
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

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isEmpty()) return "--";
        try {
            // Tenta converter de ISO-8601 (UTC) para Date
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdfEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfEntrada.parse(dataIso);

            if (date == null) return dataIso;

            // Formata para o padrão brasileiro usando o fuso horário do dispositivo
            SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
            sdfSaida.setTimeZone(TimeZone.getDefault());
            return sdfSaida.format(date);
        } catch (Exception e) {
            try {
                // Tenta formato alternativo sem milissegundos
                SimpleDateFormat sdfEntrada2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = sdfEntrada2.parse(dataIso);
                if (date == null) return dataIso;
                SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
                return sdfSaida.format(date);
            } catch (Exception e2) {
                return dataIso;
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaManutencoes != null ? listaManutencoes.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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

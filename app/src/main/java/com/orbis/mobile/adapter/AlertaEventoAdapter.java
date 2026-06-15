package com.orbis.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.model.AlertaEvento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AlertaEventoAdapter extends RecyclerView.Adapter<AlertaEventoAdapter.ViewHolder> {

    private List<AlertaEvento> eventos;

    public AlertaEventoAdapter(List<AlertaEvento> eventos) {
        this.eventos = eventos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alerta_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertaEvento evento = eventos.get(position);
        
        if (evento.getUsuario() != null) {
            holder.txtUsuario.setText(evento.getUsuario().getNome());
            holder.txtRole.setText(evento.getUsuario().getRole());
            
            // Estilo baseado na role
            if ("ADMIN".equals(evento.getUsuario().getRole())) {
                holder.txtRole.setTextColor(holder.itemView.getContext().getColor(R.color.statusRed));
                holder.txtRole.setBackgroundResource(R.drawable.badge_outline_red);
            } else {
                holder.txtRole.setTextColor(holder.itemView.getContext().getColor(R.color.purple));
                holder.txtRole.setBackgroundResource(R.drawable.badge_outline_purple);
            }
        } else {
            holder.txtUsuario.setText("Sistema");
            holder.txtRole.setVisibility(View.GONE);
        }

        holder.txtData.setText(formatarData(evento.getCriadoEm()));
        holder.txtMensagem.setText(evento.getMensagem());
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public void updateList(List<AlertaEvento> novosEventos) {
        this.eventos = novosEventos;
        notifyDataSetChanged();
    }

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isEmpty()) return "--";
        try {
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdfEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfEntrada.parse(dataIso);
            SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
            return sdfSaida.format(date);
        } catch (Exception e) {
            return dataIso;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtData, txtRole, txtMensagem;

        ViewHolder(View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtEventoUsuario);
            txtData = itemView.findViewById(R.id.txtEventoData);
            txtRole = itemView.findViewById(R.id.txtEventoRole);
            txtMensagem = itemView.findViewById(R.id.txtEventoMensagem);
        }
    }
}

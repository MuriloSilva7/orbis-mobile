package com.orbis.mobile.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.orbis.mobile.R;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.ui.main.AlertaDetalheActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        TextView txtStatusVariavel;
        TextView txtIntegridadeAlerta;
        LinearProgressIndicator progressIntegridadeAlerta;
        MaterialButton btnVerMais;
        ImageView imgAlerta;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            txtStatusVariavel = itemView.findViewById(R.id.txtStatusVariavel);
            txtIntegridadeAlerta = itemView.findViewById(R.id.txtIntegridadeAlerta);
            progressIntegridadeAlerta = itemView.findViewById(R.id.progressIntegridadeAlerta);
            btnVerMais = (MaterialButton) itemView.findViewById(R.id.btnVerMais);
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

            // --- LÓGICA DA BARRA DE INTEGRIDADE ---
            float integridade = alerta.getMaquina().getIntegridade();
            holder.txtIntegridadeAlerta.setText(String.format(Locale.getDefault(), "Integridade: %.0f%%", integridade));
            holder.progressIntegridadeAlerta.setProgress((int) integridade);

            int cor;
            if (integridade >= 70) {
                cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusGreen);
            } else if (integridade >= 30) {
                cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusOrange);
            } else {
                cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusRed);
            }
            holder.progressIntegridadeAlerta.setIndicatorColor(cor);

            if (alerta.getMaquina().getImagem() != null && !alerta.getMaquina().getImagem().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(alerta.getMaquina().getImagem())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.imgAlerta);
            }
        } else {
            holder.txtNomeVariavel.setText(holder.itemView.getContext().getString(R.string.label_sem_dados));
            holder.txtIntegridadeAlerta.setText("Integridade: --");
            holder.progressIntegridadeAlerta.setProgress(0);
        }

        // --- STATUS Badge ---
        String status = alerta.getStatus();
        if (status == null) status = "DESCONHECIDO";
        
        switch (status.toUpperCase()) {
            case "ATIVO":
                holder.txtStatusVariavel.setText(holder.itemView.getContext().getString(R.string.status_ativo));
                holder.txtStatusVariavel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusRed));
                holder.txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_red);
                break;
            case "EM_ANDAMENTO":
                holder.txtStatusVariavel.setText(holder.itemView.getContext().getString(R.string.status_em_andamento));
                holder.txtStatusVariavel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusOrange));
                holder.txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_purple);
                break;
            case "RESOLVIDO":
                holder.txtStatusVariavel.setText(holder.itemView.getContext().getString(R.string.status_resolvido));
                holder.txtStatusVariavel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusGreen));
                holder.txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_green);
                break;
            default:
                holder.txtStatusVariavel.setText(status.replace("_", " "));
                holder.txtStatusVariavel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
                holder.txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_gray);
                break;
        }

        holder.btnVerMais.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AlertaDetalheActivity.class);
            intent.putExtra("id_alerta", alerta.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}

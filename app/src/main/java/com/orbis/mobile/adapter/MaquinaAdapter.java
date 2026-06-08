package com.orbis.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.orbis.mobile.R;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.ui.main.MaquinaDetalheActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MaquinaAdapter extends RecyclerView.Adapter<MaquinaAdapter.MaquinaViewHolder> {

    private List<Maquina> listaMaquinas;
    private List<Maquina> listaOriginal;

    public MaquinaAdapter(List<Maquina> listaMaquinas){
        this.listaMaquinas = listaMaquinas;
        this.listaOriginal = new ArrayList<>(listaMaquinas);
    }

    public void atualizarLista(List<Maquina> novaLista) {
        this.listaMaquinas = novaLista;
        this.listaOriginal = new ArrayList<>(novaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        List<Maquina> filtrada = new ArrayList<>();
        if (texto.isEmpty()) {
            filtrada.addAll(listaOriginal);
        } else {
            String query = texto.toLowerCase().trim();
            for (Maquina item : listaOriginal) {
                if (item.getNome().toLowerCase().contains(query)) {
                    filtrada.add(item);
                }
            }
        }
        this.listaMaquinas = filtrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaquinaAdapter.MaquinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maquina, parent, false);
        return new MaquinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaquinaViewHolder holder, int position) {
        Maquina maquina = listaMaquinas.get(position);
        holder.txtNomeVariavel.setText(maquina.getNome());

        float integridade = maquina.getIntegridade();
        holder.txtIntegridadePercent.setText(String.format(Locale.getDefault(), "Integridade: %.0f%%", integridade));
        holder.progressIntegridade.setProgress((int) integridade);

        // Define a cor da barra de acordo com a porcentagem
        int cor;
        if (integridade >= 70) {
            cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusGreen);
        } else if (integridade >= 30) {
            cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusOrange);
        } else {
            cor = ContextCompat.getColor(holder.itemView.getContext(), R.color.statusRed);
        }
        holder.progressIntegridade.setIndicatorColor(cor);

        Glide.with(holder.itemView.getContext())
                .load(maquina.getImagem())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imgMaquina);

        holder.btnVerMais.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MaquinaDetalheActivity.class);
            intent.putExtra("id_maquina", maquina.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaMaquinas != null ? listaMaquinas.size() : 0;
    }

    public static class MaquinaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMaquina;
        TextView txtNomeVariavel, txtIntegridadePercent;
        LinearProgressIndicator progressIntegridade;
        MaterialButton btnVerMais;

        public MaquinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            txtIntegridadePercent = itemView.findViewById(R.id.txtIntegridadePercent);
            progressIntegridade = itemView.findViewById(R.id.progressIntegridade);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
            imgMaquina = itemView.findViewById(R.id.imgMaquina);
        }
    }
}

package com.orbis.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.orbis.mobile.R;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.ui.main.MaquinaDetalheActivity;

import java.util.ArrayList;
import java.util.List;

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

        Glide.with(holder.itemView.getContext())
                .load(maquina.getImagem())
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
        TextView txtNomeVariavel;
        MaterialButton btnVerMais;

        public MaquinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = (MaterialButton) itemView.findViewById(R.id.btnVerMais);
            imgMaquina = itemView.findViewById(R.id.imgMaquina);
        }
    }
}

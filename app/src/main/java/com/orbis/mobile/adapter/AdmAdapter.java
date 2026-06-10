package com.orbis.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.ui.main.TecnicoDetalheActivity;

import java.util.ArrayList;
import java.util.List;

public class AdmAdapter extends RecyclerView.Adapter<AdmAdapter.AdmViewHolder> {

    private List<Usuario> listaAdms;
    private List<Usuario> listaOriginal;

    public AdmAdapter(List<Usuario> listaAdms) {
        this.listaAdms = listaAdms;
        this.listaOriginal = new ArrayList<>(listaAdms);
    }

    public void atualizarLista(List<Usuario> novaLista) {
        this.listaAdms = novaLista;
        this.listaOriginal = new ArrayList<>(novaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        List<Usuario> filtrada = new ArrayList<>();
        if (texto.isEmpty()) {
            filtrada.addAll(listaOriginal);
        } else {
            String query = texto.toLowerCase().trim();
            for (Usuario item : listaOriginal) {
                if (item.getNome().toLowerCase().contains(query)) {
                    filtrada.add(item);
                }
            }
        }
        this.listaAdms = filtrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adm, parent, false);
        return new AdmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdmViewHolder holder, int position) {
        Usuario adm = listaAdms.get(position);
        holder.txtNomeAdm.setText(adm.getNome());

        Glide.with(holder.itemView.getContext())
                .load(adm.getFotoPerfil())
                .placeholder(R.drawable.ic_perfil)
                .error(R.drawable.ic_perfil)
                .circleCrop()
                .into(holder.imgAdm);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TecnicoDetalheActivity.class);
            intent.putExtra("id_tecnico", adm.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaAdms != null ? listaAdms.size() : 0;
    }

    public static class AdmViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeAdm;
        ImageView imgAdm;

        public AdmViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeAdm = itemView.findViewById(R.id.txtNomeAdm);
            imgAdm = itemView.findViewById(R.id.imgAdm);
        }
    }
}
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
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.ui.main.TecnicoDetalheActivity;

import java.util.ArrayList;
import java.util.List;

public class TecnicoAdapter extends RecyclerView.Adapter<TecnicoAdapter.TecnicoViewHolder> {

    private List<Usuario> listaTecnicos;
    private List<Usuario> listaOriginal;

    public TecnicoAdapter(List<Usuario> listaTecnicos) {
        this.listaTecnicos = listaTecnicos;
        this.listaOriginal = new ArrayList<>(listaTecnicos);
    }

    public void atualizarLista(List<Usuario> novaLista) {
        this.listaTecnicos = novaLista;
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
        this.listaTecnicos = filtrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TecnicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tecnico, parent, false);
        return new TecnicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TecnicoViewHolder holder, int position) {
        Usuario tecnico = listaTecnicos.get(position);
        holder.txtNomeVariavel.setText(tecnico.getNome());

        Glide.with(holder.itemView.getContext())
                .load(tecnico.getFotoPerfil())
                .placeholder(R.drawable.ic_perfil)
                .error(R.drawable.ic_perfil)
                .circleCrop()
                .into(holder.imgTecnico);

        holder.btnVerMais.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TecnicoDetalheActivity.class);
            intent.putExtra("id_tecnico", tecnico.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaTecnicos != null ? listaTecnicos.size() : 0;
    }

    public static class TecnicoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeVariavel;
        MaterialButton btnVerMais;
        ImageView imgTecnico;

        public TecnicoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = (MaterialButton) itemView.findViewById(R.id.btnVerMais);
            imgTecnico = itemView.findViewById(R.id.imgTecnico);
        }
    }
}

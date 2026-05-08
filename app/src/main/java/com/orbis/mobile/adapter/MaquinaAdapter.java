package com.orbis.mobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.ui.main.MaquinaDetalheActivity;

import java.util.List;

public class MaquinaAdapter extends RecyclerView.Adapter<MaquinaAdapter.MaquinaViewHolder> {

    private List<Maquina> listaMaquinas;

    public MaquinaAdapter(List<Maquina> listaMaquinas){
        this.listaMaquinas = listaMaquinas;
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

        TextView txtNomeVariavel;
        Button btnVerMais;

        public MaquinaViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
        }
    }
}

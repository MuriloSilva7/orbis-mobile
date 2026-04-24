package com.example.orbis.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orbis.R;
import com.example.orbis.model.Usuario;
import com.example.orbis.ui.main.TecnicoDetalheActivity;

import java.util.List;

public class TecnicoAdapter extends RecyclerView.Adapter<TecnicoAdapter.TecnicoViewHolder> {

    private List<Usuario> listaTecnicos;

    public TecnicoAdapter(List<Usuario> listaTecnicos) {
        this.listaTecnicos = listaTecnicos;
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
        Button btnVerMais;

        public TecnicoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNomeVariavel = itemView.findViewById(R.id.txtNomeVariavel);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
        }
    }
}

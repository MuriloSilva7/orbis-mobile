package com.orbis.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.model.Sensor;
import com.orbis.mobile.model.TecnicosResponse;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private TextInputEditText editSearch;
    private LinearProgressIndicator progressBar;
    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private SearchResultAdapter adapter;
    
    private List<Object> allResults = new ArrayList<>();
    private List<Object> filteredResults = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editSearch = view.findViewById(R.id.editSearchGlobal);
        progressBar = view.findViewById(R.id.progressSearch);
        recyclerView = view.findViewById(R.id.recyclerSearchResults);
        txtEmpty = view.findViewById(R.id.txtEmptySearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultAdapter(filteredResults, item -> {
            abrirDetalhe(item);
            dismiss();
        });
        recyclerView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        progressBar.setVisibility(View.VISIBLE);
        OrbisApiService api = RetrofitClient.getInstance(requireContext()).getApi();

        // Carrega máquinas
        api.getMaquinas().enqueue(new Callback<List<Maquina>>() {
            @Override
            public void onResponse(Call<List<Maquina>> call, Response<List<Maquina>> response) {
                if (response.isSuccessful() && response.body() != null) allResults.addAll(response.body());
                checkLoadingFinished();
            }
            @Override public void onFailure(Call<List<Maquina>> call, Throwable t) { checkLoadingFinished(); }
        });

        // Carrega Alertas
        api.getAlertas().enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                if (response.isSuccessful() && response.body() != null) allResults.addAll(response.body());
                checkLoadingFinished();
            }
            @Override public void onFailure(Call<List<Alerta>> call, Throwable t) { checkLoadingFinished(); }
        });

        // Carrega Técnicos
        api.getTecnicos(1, 100).enqueue(new Callback<TecnicosResponse>() {
            @Override
            public void onResponse(Call<TecnicosResponse> call, Response<TecnicosResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDados() != null) allResults.addAll(response.body().getDados());
                checkLoadingFinished();
            }
            @Override public void onFailure(Call<TecnicosResponse> call, Throwable t) { checkLoadingFinished(); }
        });

        // Carrega Sensores
        api.getSensores().enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(Call<List<Sensor>> call, Response<List<Sensor>> response) {
                if (response.isSuccessful() && response.body() != null) allResults.addAll(response.body());
                checkLoadingFinished();
            }
            @Override public void onFailure(Call<List<Sensor>> call, Throwable t) { checkLoadingFinished(); }
        });
    }

    private int responseCount = 0;
    private void checkLoadingFinished() {
        responseCount++;
        if (responseCount >= 4) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filtrar(String query) {
        filteredResults.clear();
        if (query.length() < 2) {
            adapter.notifyDataSetChanged();
            txtEmpty.setVisibility(View.GONE);
            return;
        }

        String q = query.toLowerCase();
        for (Object obj : allResults) {
            if (obj instanceof Maquina) {
                if (((Maquina) obj).getNome().toLowerCase().contains(q)) filteredResults.add(obj);
            } else if (obj instanceof Alerta) {
                if (((Alerta) obj).getMensagem().toLowerCase().contains(q)) filteredResults.add(obj);
            } else if (obj instanceof Usuario) {
                if (((Usuario) obj).getNome().toLowerCase().contains(q)) filteredResults.add(obj);
            } else if (obj instanceof Sensor) {
                if (((Sensor) obj).getTipo().toLowerCase().contains(q)) filteredResults.add(obj);
            }
        }

        adapter.notifyDataSetChanged();
        txtEmpty.setVisibility(filteredResults.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void abrirDetalhe(Object item) {
        Intent intent = null;
        if (item instanceof Maquina) {
            intent = new Intent(getContext(), MaquinaDetalheActivity.class);
            intent.putExtra("id_maquina", ((Maquina) item).getId());
        } else if (item instanceof Alerta) {
            intent = new Intent(getContext(), AlertaDetalheActivity.class);
            intent.putExtra("id_alerta", ((Alerta) item).getId());
        } else if (item instanceof Usuario) {
            intent = new Intent(getContext(), TecnicoDetalheActivity.class);
            intent.putExtra("id_tecnico", ((Usuario) item).getId());
        } else if (item instanceof Sensor) {
            intent = new Intent(getContext(), SensorDetalheActivity.class);
            intent.putExtra("id_sensor", ((Sensor) item).getId());
        }
        if (intent != null) startActivity(intent);
    }

    private static class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.VH> {
        private final List<Object> items;
        private final OnItemClickListener listener;
        interface OnItemClickListener { void onItemClick(Object item); }

        SearchResultAdapter(List<Object> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Object item = items.get(position);
            if (item instanceof Maquina) {
                holder.text1.setText(((Maquina) item).getNome());
                holder.text2.setText(holder.itemView.getContext().getString(R.string.title_maquinas) + " • " + ((Maquina) item).getSetor());
            } else if (item instanceof Alerta) {
                holder.text1.setText(holder.itemView.getContext().getString(R.string.label_ocorrencia) + " #" + ((Alerta) item).getId());
                holder.text2.setText(holder.itemView.getContext().getString(R.string.title_alertas) + " • " + ((Alerta) item).getTipo());
            } else if (item instanceof Usuario) {
                holder.text1.setText(((Usuario) item).getNome());
                holder.text2.setText(holder.itemView.getContext().getString(R.string.label_tecnico) + " • " + ((Usuario) item).getEspecialidade());
            } else if (item instanceof Sensor) {
                holder.text1.setText(holder.itemView.getContext().getString(R.string.label_sensor) + " " + ((Sensor) item).getTipo());
                holder.text2.setText(holder.itemView.getContext().getString(R.string.label_status) + ": " + ((Sensor) item).getStatus());
            }
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView text1, text2;
            VH(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}

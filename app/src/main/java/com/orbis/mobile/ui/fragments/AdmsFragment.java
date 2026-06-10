package com.orbis.mobile.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.orbis.mobile.R;
import com.orbis.mobile.adapter.TecnicoAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.TecnicosResponse;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdmsFragment extends Fragment {

    private RecyclerView recyclerAdms;
    private TecnicoAdapter adapter;
    private List<Usuario> listaAdms = new ArrayList<>();
    private LinearProgressIndicator progressBar;
    private TextInputEditText editSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adms, container, false);

        recyclerAdms = view.findViewById(R.id.recyclerAdms);
        progressBar = view.findViewById(R.id.progressAdms);
        editSearch = view.findViewById(R.id.editSearchAdms);

        MaterialButton btnRefresh = view.findViewById(R.id.btnRefreshAdms);
        btnRefresh.setOnClickListener(v -> carregarAdms());

        recyclerAdms.setLayoutManager(new LinearLayoutManager(getContext()));
        // Reutilizando o TecnicoAdapter já que a estrutura visual é a mesma
        adapter = new TecnicoAdapter(listaAdms);
        recyclerAdms.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        carregarAdms();
        return view;
    }

    private void carregarAdms() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        OrbisApiService apiService = RetrofitClient.getInstance(requireContext()).getApi();
        
        // Assumindo que buscaremos da mesma fonte e filtraremos pela role ADMIN
        // Se houver um endpoint específico no futuro, basta alterar aqui.
        apiService.getAdmins(1, 100).enqueue(new Callback<TecnicosResponse>() {
            @Override
            public void onResponse(Call<TecnicosResponse> call, Response<TecnicosResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> todos = response.body().getDados();
                    listaAdms.clear();
                    
                    // Filtra apenas usuários com role ADMIN ou ADM
                    for (Usuario u : todos) {
                        if ("ADMIN".equalsIgnoreCase(u.getRole()) || "ADM".equalsIgnoreCase(u.getRole())) {
                            listaAdms.add(u);
                        }
                    }
                    
                    adapter.atualizarLista(listaAdms);
                }
            }

            @Override
            public void onFailure(Call<TecnicosResponse> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro ao carregar administradores", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

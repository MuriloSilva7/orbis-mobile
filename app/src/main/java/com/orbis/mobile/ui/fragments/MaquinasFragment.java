package com.orbis.mobile.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.orbis.mobile.R;
import com.orbis.mobile.adapter.MaquinaAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MaquinasFragment extends Fragment {

    private RecyclerView recyclerMaquinas;
    private MaquinaAdapter adapter;
    private List<Maquina> listaMaquinas = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText editSearch;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maquinas, container, false);

        recyclerMaquinas = view.findViewById(R.id.recyclerMaquinas);
        progressBar = view.findViewById(R.id.progressMaquinas);
        editSearch = view.findViewById(R.id.editSearchMaquinas);

        ImageButton btnRefresh = view.findViewById(R.id.btnRefreshMaquinas);
        btnRefresh.setOnClickListener(v -> carregarMaquinas());

        recyclerMaquinas.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new MaquinaAdapter(listaMaquinas);
        recyclerMaquinas.setAdapter(adapter);

        setupSearch();
        carregarMaquinas();

        return view;
    }

    private void setupSearch() {
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
    }

    private void carregarMaquinas() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        OrbisApiService apiService = RetrofitClient
                .getInstance(requireContext())
                .getApi();

        Call<List<Maquina>> call = apiService.getMaquinas();

        call.enqueue(new Callback<List<Maquina>>() {
            @Override
            public void onResponse(Call<List<Maquina>> call,
                                   Response<List<Maquina>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    listaMaquinas = response.body();
                    adapter.atualizarLista(listaMaquinas);
                }
            }

            @Override
            public void onFailure(Call<List<Maquina>> call,
                                  Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("ERRO_API", t.getMessage());
            }
        });
    }
}
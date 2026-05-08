package com.orbis.mobile.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orbis.mobile.R;
import com.orbis.mobile.adapter.AlertaAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaAlertasFragment extends Fragment {

    private static final String ARG_STATUS = "status";

    private String statusFiltro;
    private RecyclerView recyclerView;
    private AlertaAdapter adapter;
    private List<Alerta> listaFiltrada = new ArrayList<>();

    public static ListaAlertasFragment newInstance(String status) {
        ListaAlertasFragment fragment = new ListaAlertasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            statusFiltro = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_alertas, container, false);

        recyclerView = view.findViewById(R.id.recyclerAlertas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AlertaAdapter(listaFiltrada);
        recyclerView.setAdapter(adapter);

        carregarAlertas();

        return view;
    }

    private void carregarAlertas() {

        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<List<Alerta>> call = apiService.getAlertas();

        call.enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call,
                                   Response<List<Alerta>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaFiltrada.clear();

                    for (Alerta alerta : response.body()) {

                        if (alerta.getStatus().equalsIgnoreCase(statusFiltro)) {
                            listaFiltrada.add(alerta);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call,
                                  Throwable t) {

                Log.e("ERRO_API", t.getMessage());
            }
        });
    }
}
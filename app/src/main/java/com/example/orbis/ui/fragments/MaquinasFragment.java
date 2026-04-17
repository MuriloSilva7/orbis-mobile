package com.example.orbis.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orbis.R;
import com.example.orbis.adapter.MaquinaAdapter;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Maquina;
import com.example.orbis.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MaquinasFragment extends Fragment {

    private RecyclerView recyclerMaquinas;
    private MaquinaAdapter adapter;
    private List<Maquina> listaMaquinas;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maquinas, container, false);

        recyclerMaquinas = view.findViewById(R.id.recyclerMaquinas);

        recyclerMaquinas.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        listaMaquinas = new ArrayList<>();

        adapter = new MaquinaAdapter(listaMaquinas);
        recyclerMaquinas.setAdapter(adapter);

        carregarMaquinas();

        return view;
    }

    private void carregarMaquinas() {
        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<List<Maquina>> call = apiService.getMaquinas();

        call.enqueue(new Callback<List<Maquina>>() {
            @Override
            public void onResponse(Call<List<Maquina>> call,
                                   Response<List<Maquina>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaMaquinas.clear();
                    listaMaquinas.addAll(response.body());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Maquina>> call,
                                  Throwable t) {

                Log.e("ERRO_API", t.getMessage());
            }
        });
    }
}
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
import com.example.orbis.adapter.SensorAdapter;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Maquina;
import com.example.orbis.model.Sensor;
import com.example.orbis.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensoresFragment extends Fragment {

    private RecyclerView recyclerSensores;
    private SensorAdapter adapter;
    private List<Sensor> listaSensores;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sensores, container, false);

        recyclerSensores = view.findViewById(R.id.recyclerSensores);

        recyclerSensores.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        listaSensores = new ArrayList<>();

        adapter = new SensorAdapter(listaSensores);
        recyclerSensores.setAdapter(adapter);

        carregarSensores();

        return view;
    }

    private void carregarSensores() {
        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<List<Sensor>> call = apiService.getSensores();

        call.enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(Call<List<Sensor>> call,
                                   Response<List<Sensor>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaSensores.clear();
                    listaSensores.addAll(response.body());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Sensor>> call,
                                  Throwable t) {

                Log.e("ERRO_API", t.getMessage());
            }
        });
    }
}
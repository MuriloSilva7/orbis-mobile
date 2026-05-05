package com.example.orbis.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orbis.R;
import com.example.orbis.adapter.TecnicoAdapter;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.TecnicosResponse;
import com.example.orbis.model.Usuario;
import com.example.orbis.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TecnicosFragment extends Fragment {

    private RecyclerView recyclerTecnicos;
    private TecnicoAdapter adapter;
    private List<Usuario> listaTecnicos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tecnicos, container, false);

        recyclerTecnicos = view.findViewById(R.id.recyclerTecnicos);
        recyclerTecnicos.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TecnicoAdapter(listaTecnicos);
        recyclerTecnicos.setAdapter(adapter);

        carregarTecnicos();

        return view;
    }

    private void carregarTecnicos() {

        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<TecnicosResponse> call = apiService.getTecnicos(1, 10);

        call.enqueue(new Callback<TecnicosResponse>() {

            @Override
            public void onResponse(Call<TecnicosResponse> call,
                                   Response<TecnicosResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaTecnicos.clear();
                    listaTecnicos.addAll(response.body().getDados());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<TecnicosResponse> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Erro: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.orbis.mobile.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orbis.mobile.R;
import com.orbis.mobile.adapter.TecnicoAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.TecnicosResponse;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TecnicosFragment extends Fragment {

    private RecyclerView recyclerTecnicos;
    private TecnicoAdapter adapter;
    private List<Usuario> listaTecnicos = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tecnicos, container, false);

        recyclerTecnicos = view.findViewById(R.id.recyclerTecnicos);
        progressBar = view.findViewById(R.id.progressTecnicos);
        
        recyclerTecnicos.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TecnicoAdapter(listaTecnicos);
        recyclerTecnicos.setAdapter(adapter);

        carregarTecnicos();

        return view;
    }

    private void carregarTecnicos() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        OrbisApiService apiService = RetrofitClient
                .getInstance(requireContext())
                .getApi();

        Call<TecnicosResponse> call = apiService.getTecnicos(1, 10);

        call.enqueue(new Callback<TecnicosResponse>() {

            @Override
            public void onResponse(Call<TecnicosResponse> call,
                                   Response<TecnicosResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    listaTecnicos.clear();
                    listaTecnicos.addAll(response.body().getDados());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<TecnicosResponse> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),
                        "Erro: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
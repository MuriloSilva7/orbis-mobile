package com.orbis.mobile.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import com.orbis.mobile.R;
import com.orbis.mobile.adapter.SensorAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Sensor;
import com.orbis.mobile.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensoresFragment extends Fragment {

    private RecyclerView recyclerSensores;
    private SensorAdapter adapter;
    private List<Sensor> listaSensores = new ArrayList<>();
    private ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensores, container, false);

        recyclerSensores = view.findViewById(R.id.recyclerSensores);
        progressBar = view.findViewById(R.id.progressSensores);

        ImageButton btnRefresh = view.findViewById(R.id.btnRefreshSensores);
        btnRefresh.setOnClickListener(v -> carregarSensores());

        recyclerSensores.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SensorAdapter(listaSensores);
        recyclerSensores.setAdapter(adapter);

        carregarSensores();
        return view;
    }

    private void carregarSensores() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        OrbisApiService apiService = RetrofitClient.getInstance(requireContext()).getApi();
        apiService.getSensores().enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(Call<List<Sensor>> call, Response<List<Sensor>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    listaSensores.clear();
                    listaSensores.addAll(response.body());
                    adapter.atualizarLista(listaSensores);
                }
            }
            @Override
            public void onFailure(Call<List<Sensor>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("ERRO_API", t.getMessage());
            }
        });
    }
}

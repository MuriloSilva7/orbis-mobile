package com.example.orbis.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orbis.R;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Usuario;
import com.example.orbis.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PerfilFragment extends Fragment {

    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtRole;
    private TextView txtAtivo;
    private TextView txtTelefone;
    private TextView txtEspecialidade;

    public PerfilFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        txtNome = view.findViewById(R.id.txtNomeVariavel);
        txtEmail = view.findViewById(R.id.txtEmailVariavel);
        txtRole = view.findViewById(R.id.txtCargoVariavel);
        txtAtivo = view.findViewById(R.id.txtEstadoVariavel);
        txtTelefone = view.findViewById(R.id.txtTelefoneVariavel);
        txtEspecialidade = view.findViewById(R.id.txtEspecialidadeVariavel);

        carregarPerfil();

        return view;
    }

    private void carregarPerfil() {

        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        apiService.getPerfil().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Usuario usuario = response.body();

                    txtNome.setText(usuario.getNome());
                    txtEmail.setText(usuario.getEmail());
                    txtRole.setText(usuario.getRole());
                    txtTelefone.setText(usuario.getTelefone());
                    txtEspecialidade.setText(usuario.getEspecialidade());

                    if (usuario.isAtivo()) {
                        txtAtivo.setText("Ativo");
                    } else {
                        txtAtivo.setText("Inativo");
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Erro ao carregar perfil: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.orbis.mobile.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

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
    private ImageView imgUsuario;

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
        imgUsuario = view.findViewById(R.id.imgUsuario);

        carregarPerfil();

        return view;
    }

    private void carregarPerfil() {

        OrbisApiService apiService = RetrofitClient
                .getInstance(requireContext())
                .getApi();

        apiService.getPerfil().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Usuario usuario = response.body();

                    Glide.with(requireContext())
                            .load(usuario.getFotoPerfil())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imgUsuario);

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
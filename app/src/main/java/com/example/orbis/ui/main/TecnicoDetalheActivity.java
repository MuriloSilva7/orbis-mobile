package com.example.orbis.ui.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.orbis.R;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Maquina;
import com.example.orbis.model.Usuario;
import com.example.orbis.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnicoDetalheActivity extends AppCompatActivity {

    private TextView txtCargoVariavel;
    private TextView txtNomeVariavel;
    private TextView txtEspecialidadeVariavel;
    private TextView txtTelefoneVariavel;
    private TextView txtEmailVariavel;
    private TextView txtEstadoVariavel;

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico_detalhe);

        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());

        txtCargoVariavel = findViewById(R.id.txtCargoVariavel);
        txtNomeVariavel = findViewById(R.id.txtNomeVariavel);
        txtEspecialidadeVariavel = findViewById(R.id.txtEspecialidadeVariavel);
        txtTelefoneVariavel = findViewById(R.id.txtTelefoneVariavel);
        txtEmailVariavel = findViewById(R.id.txtEmailVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);

        // Mesmo nome usado no Adapter
        int id = getIntent().getIntExtra("id_tecnico", -1);

        if (id != -1) {
            carregarDetalhes(id);
        }
    }

    private void carregarDetalhes(int id) {

        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<Usuario> call = apiService.getUsuario(id);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Usuario tecnico = response.body();

                    txtCargoVariavel.setText(tecnico.getRole());
                    txtNomeVariavel.setText(tecnico.getNome());
                    txtEspecialidadeVariavel.setText(tecnico.getEspecialidade());
                    txtTelefoneVariavel.setText(tecnico.getTelefone());
                    txtEmailVariavel.setText(tecnico.getEmail());

                    txtEstadoVariavel.setText(
                            tecnico.isAtivo() ? "Ativo" : "Inativo"
                    );
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

                Toast.makeText(
                        TecnicoDetalheActivity.this,
                        "Erro ao carregar técnico: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
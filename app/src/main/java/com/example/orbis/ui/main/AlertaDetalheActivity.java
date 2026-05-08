package com.example.orbis.ui.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.orbis.R;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Alerta;
import com.example.orbis.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlertaDetalheActivity extends AppCompatActivity {

    private TextView txtIdAlertaVariavel;
    private TextView txtIdSensorVariavel;
    private TextView txtIdMaquinaVariavel;
    private TextView txtIdTecnicoVariavel;

    private TextView txtMaquinaVariavel;
    private TextView txtSensorVariavel;
    private TextView txtCriadoEmVariavel;
    private TextView txtTipoVariavel;
    private TextView txtStatusVariavel;
    private TextView txtMensagemVariavel;

    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_detalhe);

        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        txtIdAlertaVariavel =
                findViewById(R.id.txtIdAlertaVariavel);

        txtIdSensorVariavel =
                findViewById(R.id.txtIdSensorVariavel);

        txtIdMaquinaVariavel =
                findViewById(R.id.txtIdMaquinaVariavel);

        txtIdTecnicoVariavel =
                findViewById(R.id.txtIdTecnicoVariavel);

        txtMaquinaVariavel =
                findViewById(R.id.txtMaquinaVariavel);

        txtSensorVariavel =
                findViewById(R.id.txtSensorVariavel);

        txtCriadoEmVariavel =
                findViewById(R.id.txtCriadoEmVariavel);

        txtTipoVariavel =
                findViewById(R.id.txtTipoVariavel);

        txtStatusVariavel =
                findViewById(R.id.txtStatusVariavel);

        txtMensagemVariavel =
                findViewById(R.id.txtMensagemVariavel);

        int id = getIntent().getIntExtra("id_alerta", -1);

        carregarDetalhes(id);
    }

    private void carregarDetalhes(int id) {

        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<List<Alerta>> call = apiService.getAlertas();

        call.enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call,
                                   Response<List<Alerta>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    for (Alerta alerta : response.body()) {

                        if (alerta.getId() == id) {

                            txtIdAlertaVariavel.setText(
                                    String.valueOf(alerta.getId())
                            );

                            txtIdSensorVariavel.setText(
                                    String.valueOf(
                                            alerta.getSensor().getId()
                                    )
                            );

                            txtIdMaquinaVariavel.setText(
                                    String.valueOf(
                                            alerta.getMaquina().getId()
                                    )
                            );

                            if (alerta.getTecnicoId() != null) {

                                txtIdTecnicoVariavel.setText(
                                        String.valueOf(
                                                alerta.getTecnicoId()
                                        )
                                );

                            } else {

                                txtIdTecnicoVariavel.setText(
                                        "Sem técnico"
                                );
                            }

                            txtMaquinaVariavel.setText(
                                    alerta.getMaquina().getNome()
                            );

                            txtSensorVariavel.setText(
                                    alerta.getSensor().getTipo()
                            );

                            txtCriadoEmVariavel.setText(
                                    alerta.getCriadoEm()
                            );

                            txtTipoVariavel.setText(
                                    alerta.getTipo()
                            );

                            txtStatusVariavel.setText(
                                    alerta.getStatus()
                            );

                            txtMensagemVariavel.setText(
                                    alerta.getMensagem()
                            );

                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call,
                                  Throwable t) {

            }
        });
    }
}
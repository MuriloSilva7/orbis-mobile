package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Sensor;
import com.orbis.mobile.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorDetalheActivity extends AppCompatActivity {

    private TextView txtIdSensorVariavel;
    private TextView txtIdMaquinaVariavel;
    private TextView txtTipoVariavel;
    private TextView txtUltimaTemperaturaVariavel;
    private TextView txtLimiteTemperaturaVariavel;
    private TextView txtUltimaVibracaoVariavel;
    private TextView txtLimiteVibracaoVariavel;
    private TextView txtEstadoVariavel;

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detalhe);

        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        txtIdSensorVariavel = findViewById(R.id.txtIdSensorVariavel);
        txtIdMaquinaVariavel = findViewById(R.id.txtIdMaquinaVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtUltimaTemperaturaVariavel = findViewById(R.id.txtUltimaTemperaturaVariavel);
        txtLimiteTemperaturaVariavel = findViewById(R.id.txtLimiteTemperaturaVariavel);
        txtUltimaVibracaoVariavel = findViewById(R.id.txtUltimaVibracaoVariavel);
        txtLimiteVibracaoVariavel = findViewById(R.id.txtLimiteVibracaoVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);

        int id = getIntent().getIntExtra("id_sensor", -1);
        carregarDetalhes(id);
    }

    private void carregarDetalhes(int id) {
        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<Sensor> call = apiService.getSensor(id);

        call.enqueue(new Callback<Sensor>() {
            @Override
            public void onResponse(Call<Sensor> call, Response<Sensor> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Sensor sensor = response.body();

                    txtIdSensorVariavel.setText(String.valueOf(sensor.getId()));
                    txtIdMaquinaVariavel.setText(String.valueOf(sensor.getMaquinaId()));
                    txtUltimaTemperaturaVariavel.setText(String.valueOf(sensor.getUltimaTemperatura()));
                    txtLimiteTemperaturaVariavel.setText(String.valueOf(sensor.getLimiteTemperatura()));
                    txtUltimaVibracaoVariavel.setText(String.valueOf(sensor.getUltimaVibracao()));
                    txtLimiteVibracaoVariavel.setText(String.valueOf(sensor.getLimiteVibracao()));
                    txtTipoVariavel.setText(sensor.getTipo());
                    txtEstadoVariavel.setText(sensor.getStatus());
                }
            }

            @Override
            public void onFailure(Call<Sensor> call, Throwable t) {
                Toast.makeText(SensorDetalheActivity.this,
                        "Erro ao carregar sensor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
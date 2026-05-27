package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    private ProgressBar progressSensor;

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detalhe);

        setupToolbar();

        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());

        txtIdSensorVariavel = findViewById(R.id.txtIdSensorVariavel);
        txtIdMaquinaVariavel = findViewById(R.id.txtIdMaquinaVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtUltimaTemperaturaVariavel = findViewById(R.id.txtUltimaTemperaturaVariavel);
        txtLimiteTemperaturaVariavel = findViewById(R.id.txtLimiteTemperaturaVariavel);
        txtUltimaVibracaoVariavel = findViewById(R.id.txtUltimaVibracaoVariavel);
        txtLimiteVibracaoVariavel = findViewById(R.id.txtLimiteVibracaoVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);
        progressSensor = findViewById(R.id.progressSensor);

        int id = getIntent().getIntExtra("id_sensor", -1);
        carregarDetalhes(id);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setLoading(boolean isLoading) {
        if (progressSensor != null) {
            progressSensor.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void carregarDetalhes(int id) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient
                .getInstance(this)
                .getApi();

        Call<Sensor> call = apiService.getSensor(id);

        call.enqueue(new Callback<Sensor>() {
            @Override
            public void onResponse(Call<Sensor> call, Response<Sensor> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {

                    Sensor sensor = response.body();

                    txtIdSensorVariavel.setText(String.valueOf(sensor.getId()));
                    txtIdMaquinaVariavel.setText(String.valueOf(sensor.getMaquinaId()));
                    txtUltimaTemperaturaVariavel.setText(String.valueOf(sensor.getUltimaTemperatura()));
                    txtLimiteTemperaturaVariavel.setText(String.valueOf(sensor.getLimiteTemperatura()));
                    txtUltimaVibracaoVariavel.setText(String.valueOf(sensor.getUltimaVibracao()));
                    txtLimiteVibracaoVariavel.setText(String.valueOf(sensor.getLimiteVibracao()));
                    txtTipoVariavel.setText(sensor.getTipo());
                    
                    String status = sensor.getStatus();
                    txtEstadoVariavel.setText(status);
                    if ("ATIVO".equalsIgnoreCase(status)) {
                        txtEstadoVariavel.setTextColor(getColor(R.color.statusGreen));
                        txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_green);
                    } else {
                        txtEstadoVariavel.setTextColor(getColor(R.color.statusRed));
                        txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_red);
                    }
                }
            }

            @Override
            public void onFailure(Call<Sensor> call, Throwable t) {
                setLoading(false);
                Toast.makeText(SensorDetalheActivity.this,
                        "Erro ao carregar sensor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

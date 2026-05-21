package com.orbis.mobile.model;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IaActivity extends AppCompatActivity {

    private EditText edtPergunta;
    private Button btnEnviar;
    private TextView txtResposta;

    private final List<ChatMessage> historico = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        edtPergunta = findViewById(R.id.edtPergunta);
        btnEnviar = findViewById(R.id.btnEnviar);
        txtResposta = findViewById(R.id.txtResposta);

        btnEnviar.setOnClickListener(v -> enviarPergunta());
    }

    private void enviarPergunta() {

        String pergunta = edtPergunta.getText().toString().trim();

        if (pergunta.isEmpty()) {
            return;
        }

        historico.add(new ChatMessage("user", pergunta));

        IaRequest request = new IaRequest(pergunta, historico);

        OrbisApiService api =
                RetrofitClient
                        .getInstance(this)
                        .getApi();

        txtResposta.setText("Pensando...");

        api.perguntarIa(request).enqueue(new Callback<IaResponse>() {

            @Override
            public void onResponse(Call<IaResponse> call,
                                   Response<IaResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String respostaIa =
                            response.body().getResposta();

                    historico.add(
                            new ChatMessage(
                                    "assistant",
                                    respostaIa
                            )
                    );

                    txtResposta.setText(respostaIa);

                } else {

                    txtResposta.setText("Erro ao consultar IA");

                }
            }

            @Override
            public void onFailure(Call<IaResponse> call, Throwable t) {

                txtResposta.setText(
                        "Erro: " + t.getMessage()
                );
            }
        });
    }
}
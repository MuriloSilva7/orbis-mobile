package com.orbis.mobile.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView txtNome, txtEmail, txtRole, txtAtivo, txtTelefone, txtEspecialidade;
    private ImageView imgUsuario;
    private Button btnAlterarFoto;
    private ProgressBar progressBar;
    private ActivityResultLauncher<String> imagePickerLauncher;

    public PerfilFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        processarESalvarFoto(uri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        txtNome = view.findViewById(R.id.txtNomeVariavel);
        txtEmail = view.findViewById(R.id.txtEmailVariavel);
        txtRole = view.findViewById(R.id.txtCargoVariavel);
        txtAtivo = view.findViewById(R.id.txtEstadoVariavel);
        txtTelefone = view.findViewById(R.id.txtTelefoneVariavel);
        txtEspecialidade = view.findViewById(R.id.txtEspecialidadeVariavel);
        imgUsuario = view.findViewById(R.id.imgUsuario);
        btnAlterarFoto = view.findViewById(R.id.btnAlterarFoto);
        progressBar = view.findViewById(R.id.progressPerfil);

        btnAlterarFoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        carregarPerfil();
        return view;
    }


    private void processarESalvarFoto(Uri uri) {
        try {
            // Preview local para o usuário sentir que funcionou
            Glide.with(this).load(uri).circleCrop().into(imgUsuario);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // REDUÇÃO PARA TAMANHO MÍNIMO (100x100)
            // Isso gera uma string Base64 bem pequena que qualquer servidor aceita
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // 50% de qualidade
            byte[] byteArray = outputStream.toByteArray();

            // Converte SEM prefixo e SEM quebras de linha
            String base64Image = Base64.encodeToString(byteArray, Base64.NO_WRAP);

            salvarNoServidor(base64Image);

        } catch (Exception e) {
            Log.e("PERFIL_FOTO", "Erro ao processar", e);
            Toast.makeText(getContext(), "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarNoServidor(String base64Image) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        OrbisApiService apiService = RetrofitClient.getInstance(requireContext()).getApi();

        Map<String, Object> body = new HashMap<>();
        // Tentamos os dois nomes mais comuns de campo
        body.put("fotoPerfil", base64Image);
        body.put("foto", base64Image);

        apiService.updatePerfil(body).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Foto salva com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarPerfil(); // Recarrega para ver se o servidor salvou mesmo
                } else {
                    try {
                        String errorMsg = response.errorBody().string();
                        Log.e("API_ERROR", "Status: " + response.code() + " | " + errorMsg);

                        // Se o erro for 413, o servidor bloqueou por tamanho
                        if (response.code() == 413) {
                            Toast.makeText(getContext(), "Imagem ainda é muito grande para o servidor.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Erro " + response.code() + " no servidor.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarPerfil() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        OrbisApiService apiService = RetrofitClient.getInstance(requireContext()).getApi();
        apiService.getPerfil().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    Glide.with(requireContext())
                            .load(usuario.getFotoPerfil())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .into(imgUsuario);

                    txtNome.setText(usuario.getNome());
                    txtEmail.setText(usuario.getEmail());
                    txtRole.setText(usuario.getRole());
                    txtTelefone.setText(usuario.getTelefone());
                    txtEspecialidade.setText(usuario.getEspecialidade());
                    txtAtivo.setText(usuario.isAtivo() ? "Ativo" : "Inativo");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

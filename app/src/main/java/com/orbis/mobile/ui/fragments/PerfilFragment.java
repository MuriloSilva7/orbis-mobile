package com.orbis.mobile.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView txtNome, txtEmail, txtRole, txtAtivo, txtTelefone, txtEspecialidade;
    private ImageView imgUsuario;
    private Button btnAlterarFoto;
    private LinearProgressIndicator progressBar;
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

        ImageButton btnRefresh = view.findViewById(R.id.btnRefreshPerfil);
        btnRefresh.setOnClickListener(v -> carregarPerfil());

        btnAlterarFoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        carregarPerfil();
        return view;
    }

    private void processarESalvarFoto(Uri uri) {
        try {
            // Preview local imediato para feedback ao usuário
            Glide.with(this).load(uri).circleCrop().into(imgUsuario);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] byteArray = lerBytes(inputStream);
            
            // Detecta o tipo real do arquivo (ex: image/png, image/jpeg)
            String mimeType = requireContext().getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";

            salvarNoServidor(byteArray, mimeType);
        } catch (Exception e) {
            Log.e("PERFIL_FOTO", "Erro ao processar imagem", e);
            Toast.makeText(getContext(), "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] lerBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(chunk)) != -1) {
            buffer.write(chunk, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private void salvarNoServidor(byte[] imageBytes, String mimeType) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        OrbisApiService apiService = RetrofitClient.getInstance(requireContext()).getApi();

        RequestBody requestBody = RequestBody.create(
                MediaType.parse(mimeType), imageBytes
        );
        
        // "imagem" é o nome do campo multipart esperado pela rota PUT /perfil/foto no seu backend
        MultipartBody.Part part = MultipartBody.Part.createFormData("imagem", "perfil.jpg", requestBody);

        apiService.updateFotoPerfil(part).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                    // Recarrega o perfil para sincronizar a nova URL da imagem e limpar cache do Glide
                    carregarPerfil();
                } else {
                    Log.e("API_ERROR", "Erro ao subir foto. Status: " + response.code());
                    Toast.makeText(getContext(), "Erro ao salvar foto no servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Falha na conexão", t);
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
                    
                    // Glide configurado para ignorar cache e mostrar a nova foto imediatamente
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

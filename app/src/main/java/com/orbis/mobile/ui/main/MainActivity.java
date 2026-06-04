package com.orbis.mobile.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.TokenManager;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;
import com.orbis.mobile.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host);

        NavController navController = navHostFragment.getNavController();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.maquinasFragment, R.id.alertasFragment, R.id.perfilFragment,
                R.id.sensoresFragment, R.id.tecnicosFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Mantém o título vazio para dar destaque aos elementos customizados na direita
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            toolbar.setTitle("");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        });

        setupNavHeader();
        carregarPerfilAtualizado();

        FloatingActionButton fabIa = findViewById(R.id.fabIa);
        fabIa.setOnClickListener(v -> {
            startActivity(new Intent(this, IaActivity.class));
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                confirmarLogout();
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });

        new Handler().postDelayed(() -> {
            OSDeviceState deviceState = OneSignal.getDeviceState();
            if (deviceState != null) {
                String oneSignalId = deviceState.getUserId();
                if (oneSignalId != null) {
                    enviarOneSignalId(oneSignalId);
                }
            }
        }, 5000);
    }

    private void abrirPesquisaGlobal() {
        SearchBottomSheetDialogFragment searchSheet = new SearchBottomSheetDialogFragment();
        searchSheet.show(getSupportFragmentManager(), "search_sheet");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        carregarPerfilAtualizado();
    }

    private void setupNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        ImageView imgPerfil = headerView.findViewById(R.id.imgPerfilHeader);
        TextView txtNome = headerView.findViewById(R.id.txtNomeHeader);
        TextView txtEmail = headerView.findViewById(R.id.txtEmailHeader);

        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String nome = prefs.getString("user_nome", "Usuário Orbis");
        String email = prefs.getString("user_email", "usuario@orbis.com");
        String foto = prefs.getString("user_foto", "");

        txtNome.setText(nome);
        txtEmail.setText(email);

        if (foto != null && !foto.isEmpty()) {
            Glide.with(this)
                    .load(foto)
                    .placeholder(R.drawable.ic_perfil)
                    .error(R.drawable.ic_perfil)
                    .circleCrop()
                    .into(imgPerfil);
        }
    }

    private void carregarPerfilAtualizado() {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getPerfil().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    
                    SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("user_nome", usuario.getNome())
                            .putString("user_email", usuario.getEmail())
                            .putString("user_foto", usuario.getFotoPerfil())
                            .apply();
                    
                    setupNavHeader();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("MainActivity", "Erro ao carregar perfil: " + t.getMessage());
            }
        });
    }

    private void confirmarLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_sair)
                .setMessage(R.string.msg_confirmar_logout)
                .setPositiveButton(R.string.btn_sim, (dialog, which) -> realizarLogout())
                .setNegativeButton(R.string.btn_nao, null)
                .show();
    }

    private void realizarLogout() {
        TokenManager tokenManager = new TokenManager(this);
        tokenManager.clearTokens();
        
        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void enviarOneSignalId(String oneSignalId) {
        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", "");
        Map<String, String> body = new HashMap<>();
        body.put("oneSignalId", oneSignalId);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Call<Void> call = apiService.saveDeviceToken("Bearer " + token, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("API", "OneSignal ID enviado");
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", t.getMessage());
            }
        });
    }
}

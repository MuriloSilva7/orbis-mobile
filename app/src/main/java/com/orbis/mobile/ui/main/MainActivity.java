package com.orbis.mobile.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.TokenManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Configura os destinos de nível superior
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.maquinasFragment, R.id.alertasFragment, R.id.perfilFragment,
                R.id.sensoresFragment, R.id.tecnicosFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Custom listener para o logout e navegação de activities
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_logout) {
                confirmarLogout();
                return true;
            }
            
            // Permite que o NavigationUI lide com os fragmentos padrão
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });

        // ONESIGNAL
        new Handler().postDelayed(() -> {
            OSDeviceState deviceState = OneSignal.getDeviceState();
            if (deviceState != null) {
                String oneSignalId = deviceState.getUserId();
                if (oneSignalId != null) {
                    Log.d("ONESIGNAL_ID", oneSignalId);
                    enviarOneSignalId(oneSignalId);
                }
            }
        }, 5000);
    }

    private void confirmarLogout() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja realmente sair do aplicativo?")
                .setPositiveButton("Sim", (dialog, which) -> realizarLogout())
                .setNegativeButton("Não", null)
                .show();
    }

    private void realizarLogout() {
        TokenManager tokenManager = new TokenManager(this);
        tokenManager.clearTokens();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void enviarOneSignalId(String oneSignalId) {
        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String token = prefs.getString("access_token", "");

        Map<String, String> body = new HashMap<>();
        body.put("oneSignalId", oneSignalId);

        OrbisApiService apiService = RetrofitClient
                .getInstance(this)
                .getApi();

        Call<Void> call = apiService.saveDeviceToken(
                "Bearer " + token,
                body
        );

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

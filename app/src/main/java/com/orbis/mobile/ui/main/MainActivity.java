package com.orbis.mobile.ui.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.network.RetrofitClient;
import com.orbis.mobile.ui.fragments.MaquinasFragment;
import com.orbis.mobile.ui.fragments.PerfilFragment;
import com.orbis.mobile.R;
import com.orbis.mobile.ui.fragments.AlertasFragment;
import com.orbis.mobile.ui.fragments.SensoresFragment;
import com.orbis.mobile.ui.fragments.TecnicosFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);



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



    private void enviarOneSignalId(String oneSignalId) {

        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);

        String token = prefs.getString("access_token", "");

        Map<String, String> body = new HashMap<>();
        body.put("oneSignalId", oneSignalId);

        OrbisApiService apiService = RetrofitClient
                .getInstance()
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
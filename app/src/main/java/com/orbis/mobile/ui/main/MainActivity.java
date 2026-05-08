package com.orbis.mobile.ui.main;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.orbis.mobile.ui.fragments.MaquinasFragment;
import com.orbis.mobile.ui.fragments.PerfilFragment;
import com.orbis.mobile.R;
import com.orbis.mobile.ui.fragments.AlertasFragment;
import com.orbis.mobile.ui.fragments.SensoresFragment;
import com.orbis.mobile.ui.fragments.TecnicosFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1
            );
        }

        MaterialToolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Fragment maquinasFragment = new MaquinasFragment();
        Fragment alertasFragment = new AlertasFragment();
        Fragment perfilFragment = new PerfilFragment();
        Fragment sensoresFragment = new SensoresFragment();
        Fragment tecnicosFragment = new TecnicosFragment();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);


        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);

    }
}
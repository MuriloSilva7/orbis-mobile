package com.orbis.mobile.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.ImageButton;

import com.orbis.mobile.R;
import com.orbis.mobile.adapter.AlertasTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class AlertasFragment extends Fragment {

    public AlertasFragment() {
        super(R.layout.fragment_alertas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        AlertasTabsAdapter adapter = new AlertasTabsAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            if (position == 0){
                tab.setText("Pendentes");

            } else if (position == 1){
                tab.setText("Em Andamento");
            } else {
                tab.setText("Concluídos");
            }
        }).attach();

        ImageButton btnRefresh = view.findViewById(R.id.btnRefreshAlertas);
        btnRefresh.setOnClickListener(v -> {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof ListaAlertasFragment) {
                    ((ListaAlertasFragment) fragment).carregarAlertas();
                }
            }
        });
    }
}
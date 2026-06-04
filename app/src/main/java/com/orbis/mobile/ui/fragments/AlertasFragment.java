package com.orbis.mobile.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import com.google.android.material.button.MaterialButton;

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

        EditText editSearch = view.findViewById(R.id.editSearchAlertas);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                List<Fragment> fragments = getChildFragmentManager().getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment instanceof ListaAlertasFragment) {
                        ((ListaAlertasFragment) fragment).filtrar(query);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0){
                tab.setText(R.string.tab_pendentes);
            } else if (position == 1){
                tab.setText(R.string.tab_em_andamento);
            } else {
                tab.setText(R.string.tab_concluidos);
            }
        }).attach();

        MaterialButton btnRefresh = view.findViewById(R.id.btnRefreshAlertas);
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
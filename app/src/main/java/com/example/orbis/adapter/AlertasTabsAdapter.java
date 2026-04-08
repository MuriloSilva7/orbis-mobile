package com.example.orbis.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.orbis.AlertasAndamentoTabsFragment;
import com.example.orbis.AlertasConcluidosTabsFragment;
import com.example.orbis.AlertasPendentesTabsFragment;

public class AlertasTabsAdapter extends FragmentStateAdapter {
    public AlertasTabsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0 : return new AlertasPendentesTabsFragment();
            case 1 : return new AlertasAndamentoTabsFragment();
            case 2 : return new AlertasConcluidosTabsFragment();
        }
        return new AlertasPendentesTabsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

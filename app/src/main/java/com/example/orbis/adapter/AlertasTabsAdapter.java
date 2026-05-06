package com.example.orbis.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.orbis.ui.fragments.AlertasAndamentoTabsFragment;
import com.example.orbis.ui.fragments.AlertasConcluidosTabsFragment;
import com.example.orbis.ui.fragments.AlertasPendentesTabsFragment;
import com.example.orbis.ui.fragments.ListaAlertasFragment;

public class AlertasTabsAdapter extends FragmentStateAdapter {
    public AlertasTabsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ListaAlertasFragment.newInstance("ATIVO");
            case 1:
                return ListaAlertasFragment.newInstance("EM_ANDAMENTO");
            case 2:
                return ListaAlertasFragment.newInstance("FINALIZADO"); // ou o que você usa no backend
            default:
                return ListaAlertasFragment.newInstance("ATIVO");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

package com.orbis.mobile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.orbis.mobile.ui.fragments.ListaAlertasFragment;

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
                return ListaAlertasFragment.newInstance("RESOLVIDO");
            default:
                return ListaAlertasFragment.newInstance("ATIVO");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

package dev.idm.vkp.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import dev.idm.vkp.R;
import dev.idm.vkp.fragment.AddProxyFragment;
import dev.idm.vkp.fragment.ProxyManagerFrgament;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceProvider;
import dev.idm.vkp.util.Objects;

public class ProxyManagerActivity extends NoMainActivity implements PlaceProvider {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Objects.isNull(savedInstanceState)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
                    .replace(getMainContainerViewId(), ProxyManagerFrgament.newInstance())
                    .addToBackStack("proxy-manager")
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        if (place.type == Place.PROXY_ADD) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
                    .replace(getMainContainerViewId(), AddProxyFragment.newInstance())
                    .addToBackStack("proxy-add")
                    .commit();
        }
    }
}

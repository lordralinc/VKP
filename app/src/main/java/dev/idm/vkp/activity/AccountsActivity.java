package dev.idm.vkp.activity;

import android.os.Bundle;

import dev.idm.vkp.fragment.AccountsFragment;
import dev.idm.vkp.fragment.PreferencesFragment;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceProvider;

public class AccountsActivity extends NoMainActivity implements PlaceProvider {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getMainContainerViewId(), new AccountsFragment())
                    .addToBackStack("accounts")
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        if (place.type == Place.PREFERENCES) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getMainContainerViewId(), PreferencesFragment.newInstance(place.getArgs()))
                    .addToBackStack("preferences")
                    .commit();
        }
    }

}

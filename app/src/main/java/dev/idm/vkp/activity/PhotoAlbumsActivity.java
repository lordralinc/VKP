package dev.idm.vkp.activity;

import android.content.Intent;
import android.os.Bundle;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.fragment.VKPhotoAlbumsFragment;
import dev.idm.vkp.fragment.VKPhotosFragment;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceProvider;

public class PhotoAlbumsActivity extends NoMainActivity implements PlaceProvider {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            int accountId = intent.getExtras().getInt(Extra.ACCOUNT_ID);
            int ownerId = intent.getExtras().getInt(Extra.OWNER_ID);
            String action = intent.getStringExtra(Extra.ACTION);

            VKPhotoAlbumsFragment fragment = VKPhotoAlbumsFragment.newInstance(accountId, ownerId, action, null, false);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        if (place.type == Place.VK_PHOTO_ALBUM) {
            VKPhotosFragment fragment = VKPhotosFragment.newInstance(place.getArgs());
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
                    .replace(R.id.fragment, fragment)
                    .addToBackStack("photos")
                    .commit();
        }
    }
}

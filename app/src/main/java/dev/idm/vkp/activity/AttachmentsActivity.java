package dev.idm.vkp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.fragment.DocsFragment;
import dev.idm.vkp.fragment.VideosFragment;
import dev.idm.vkp.fragment.VideosTabsFragment;
import dev.idm.vkp.model.Types;
import dev.idm.vkp.mvp.presenter.DocsListPresenter;
import dev.idm.vkp.mvp.view.IVideosListView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceProvider;

public class AttachmentsActivity extends NoMainActivity implements PlaceProvider {

    public static Intent createIntent(Context context, int accountId, int type) {
        return new Intent(context, AttachmentsActivity.class)
                .putExtra(Extra.TYPE, type)
                .putExtra(Extra.ACCOUNT_ID, accountId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Fragment fragment = null;

            int type = getIntent().getExtras().getInt(Extra.TYPE);
            int accountId = getIntent().getExtras().getInt(Extra.ACCOUNT_ID);

            switch (type) {
                case Types.DOC:
                    fragment = DocsFragment.newInstance(accountId, accountId, DocsListPresenter.ACTION_SELECT);
                    break;

                case Types.VIDEO:
                    fragment = VideosTabsFragment.newInstance(accountId, accountId, IVideosListView.ACTION_SELECT);
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                    .replace(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        if (place.type == Place.VIDEO_ALBUM) {
            Fragment fragment = VideosFragment.newInstance(place.getArgs());
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
                    .replace(R.id.fragment, fragment)
                    .addToBackStack("video_album")
                    .commit();
        }
    }
}

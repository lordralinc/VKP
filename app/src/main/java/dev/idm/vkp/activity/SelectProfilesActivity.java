package dev.idm.vkp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.SelectedProfilesAdapter;
import dev.idm.vkp.fragment.fave.FaveTabsFragment;
import dev.idm.vkp.fragment.friends.FriendsTabsFragment;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Logger;
import dev.idm.vkp.util.MainActivityTransforms;
import dev.idm.vkp.util.Utils;

public class SelectProfilesActivity extends MainActivity implements SelectedProfilesAdapter.ActionListener, ProfileSelectable {

    private static final String TAG = SelectProfilesActivity.class.getSimpleName();
    private static final String SAVE_SELECTED_OWNERS = "save_selected_owners";
    private SelectProfileCriteria mSelectableCriteria;
    private ArrayList<Owner> mSelectedOwners;
    private RecyclerView mRecyclerView;
    private SelectedProfilesAdapter mProfilesAdapter;

    public static Intent createIntent(@NonNull Context context, @NonNull Place initialPlace, @NonNull SelectProfileCriteria criteria) {
        return new Intent(context, SelectProfilesActivity.class)
                .setAction(MainActivity.ACTION_OPEN_PLACE)
                .putExtra(Extra.PLACE, initialPlace)
                .putExtra(Extra.CRITERIA, criteria);
    }

    public static Intent startFriendsSelection(@NonNull Context context) {
        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Place place = PlaceFactory.getFriendsFollowersPlace(aid, aid, FriendsTabsFragment.TAB_ALL_FRIENDS, null);

        SelectProfileCriteria criteria = new SelectProfileCriteria().setOwnerType(SelectProfileCriteria.OwnerType.ONLY_FRIENDS);

        Intent intent = new Intent(context, SelectProfilesActivity.class);
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.putExtra(Extra.PLACE, place);
        intent.putExtra(Extra.CRITERIA, criteria);
        return intent;
    }

    public static Intent startFaveSelection(@NonNull Context context) {
        int aid = Settings.get()
                .accounts()
                .getCurrent();

        Place place = PlaceFactory.getBookmarksPlace(aid, FaveTabsFragment.TAB_PAGES);

        SelectProfileCriteria criteria = new SelectProfileCriteria().setOwnerType(SelectProfileCriteria.OwnerType.OWNERS);

        Intent intent = new Intent(context, SelectProfilesActivity.class);
        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.putExtra(Extra.PLACE, place);
        intent.putExtra(Extra.CRITERIA, criteria);
        return intent;
    }

    @Override
    protected @MainActivityTransforms
    int getMainActivityTransform() {
        return MainActivityTransforms.PROFILES;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLayoutRes = R.layout.activity_main_with_profiles_selection;
        super.onCreate(savedInstanceState);
        mLastBackPressedTime = Long.MAX_VALUE - DOUBLE_BACK_PRESSED_TIMEOUT;

        mSelectableCriteria = getIntent().getParcelableExtra(Extra.CRITERIA);

        if (savedInstanceState != null) {
            mSelectedOwners = savedInstanceState.getParcelableArrayList(SAVE_SELECTED_OWNERS);
        }

        if (mSelectedOwners == null) {
            mSelectedOwners = new ArrayList<>();
        }

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mProfilesAdapter = new SelectedProfilesAdapter(this, mSelectedOwners);
        mProfilesAdapter.setActionListener(this);

        mRecyclerView = findViewById(R.id.recycleView);
        if (mRecyclerView == null) {
            throw new IllegalStateException("Invalid view");
        }

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mProfilesAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_SELECTED_OWNERS, mSelectedOwners);
    }

    @Override
    public void onClick(int adapterPosition, Owner owner) {
        mSelectedOwners.remove(mProfilesAdapter.toDataPosition(adapterPosition));
        mProfilesAdapter.notifyItemRemoved(adapterPosition);
        mProfilesAdapter.notifyHeaderChange();
    }

    @Override
    public void onCheckClick() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Extra.OWNERS, mSelectedOwners);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void select(Owner owner) {
        Logger.d(TAG, "Select, owner: " + owner);

        int index = Utils.indexOfOwner(mSelectedOwners, owner);

        if (index != -1) {
            mSelectedOwners.remove(index);
            mProfilesAdapter.notifyItemRemoved(mProfilesAdapter.toAdapterPosition(index));
        }

        mSelectedOwners.add(0, owner);
        mProfilesAdapter.notifyItemInserted(mProfilesAdapter.toAdapterPosition(0));
        mProfilesAdapter.notifyHeaderChange();
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public SelectProfileCriteria getAcceptableCriteria() {
        return mSelectableCriteria;
    }
}

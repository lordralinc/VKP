package dev.idm.vkp.fragment.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.adapter.PeopleAdapter;
import dev.idm.vkp.fragment.search.criteria.GroupSearchCriteria;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.search.CommunitiesSearchPresenter;
import dev.idm.vkp.mvp.view.search.ICommunitiesSearchView;
import dev.idm.vkp.place.PlaceFactory;

public class GroupsSearchFragment extends AbsSearchFragment<CommunitiesSearchPresenter, ICommunitiesSearchView, Community, PeopleAdapter>
        implements ICommunitiesSearchView, PeopleAdapter.ClickListener {

    public static GroupsSearchFragment newInstance(int accountId, @Nullable GroupSearchCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        GroupsSearchFragment fragment = new GroupsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(PeopleAdapter adapter, List<Community> data) {
        adapter.setItems(data);
    }

    @Override
    void postCreate(View root) {

    }

    @Override
    PeopleAdapter createAdapter(List<Community> data) {
        PeopleAdapter adapter = new PeopleAdapter(requireActivity(), data);
        adapter.setClickListener(this);
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @NotNull
    @Override
    public IPresenterFactory<CommunitiesSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunitiesSearchPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.CRITERIA),
                saveInstanceState
        );
    }

    @Override
    public void onOwnerClick(Owner owner) {
        getPresenter().fireCommunityClick((Community) owner);
    }

    @Override
    public void openCommunityWall(int accountId, Community community) {
        PlaceFactory.getOwnerWallPlace(accountId, community).tryOpenWith(requireActivity());
    }
}
package dev.idm.vkp.fragment.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.adapter.ArtistSearchAdapter;
import dev.idm.vkp.api.model.VkApiArtist;
import dev.idm.vkp.fragment.search.criteria.ArtistSearchCriteria;
import dev.idm.vkp.model.AudioArtist;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.search.ArtistSearchPresenter;
import dev.idm.vkp.mvp.view.search.IArtistSearchView;

public class ArtistSearchFragment extends AbsSearchFragment<ArtistSearchPresenter, IArtistSearchView, VkApiArtist, ArtistSearchAdapter>
        implements ArtistSearchAdapter.ClickListener, IArtistSearchView {

    public static ArtistSearchFragment newInstance(int accountId, @Nullable ArtistSearchCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ArtistSearchFragment newInstanceSelect(int accountId, @Nullable ArtistSearchCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(ArtistSearchAdapter adapter, List<VkApiArtist> data) {
        adapter.setData(data);
    }

    @Override
    void postCreate(View root) {

    }

    @Override
    ArtistSearchAdapter createAdapter(List<VkApiArtist> data) {
        ArtistSearchAdapter ret = new ArtistSearchAdapter(data, requireActivity());
        ret.setClickListener(this);
        return ret;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<ArtistSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ArtistSearchPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.CRITERIA),
                saveInstanceState
        );
    }

    @Override
    public void onArtistClick(String id) {
        getPresenter().fireArtistClick(new AudioArtist(id));
    }
}

package dev.idm.vkp.fragment.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.adapter.DialogPreviewAdapter;
import dev.idm.vkp.fragment.search.criteria.DialogsSearchCriteria;
import dev.idm.vkp.model.Conversation;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.search.DialogsSearchPresenter;
import dev.idm.vkp.mvp.view.search.IDialogsSearchView;

public class DialogsSearchFragment extends AbsSearchFragment<DialogsSearchPresenter, IDialogsSearchView, Conversation, DialogPreviewAdapter>
        implements IDialogsSearchView, DialogPreviewAdapter.ActionListener {

    public static DialogsSearchFragment newInstance(int accountId, DialogsSearchCriteria criteria) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.CRITERIA, criteria);
        DialogsSearchFragment fragment = new DialogsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NotNull
    @Override
    public IPresenterFactory<DialogsSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            DialogsSearchCriteria criteria = getArguments().getParcelable(Extra.CRITERIA);
            return new DialogsSearchPresenter(accountId, criteria, saveInstanceState);
        };
    }

    @Override
    void setAdapterData(DialogPreviewAdapter adapter, List<Conversation> data) {
        adapter.setData(data);
    }

    @Override
    void postCreate(View root) {

    }

    @Override
    DialogPreviewAdapter createAdapter(List<Conversation> data) {
        return new DialogPreviewAdapter(requireActivity(), data, this);
    }

    @Override
    RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity());
    }

    @Override
    public void onEntryClick(Conversation o) {
        getPresenter().fireEntryClick(o);
    }
}
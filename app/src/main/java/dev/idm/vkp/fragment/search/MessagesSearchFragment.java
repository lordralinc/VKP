package dev.idm.vkp.fragment.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.adapter.MessagesAdapter;
import dev.idm.vkp.fragment.search.criteria.MessageSeachCriteria;
import dev.idm.vkp.model.Keyboard;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.search.MessagesSearchPresenter;
import dev.idm.vkp.mvp.view.search.IMessagesSearchView;
import dev.idm.vkp.place.PlaceFactory;

public class MessagesSearchFragment extends AbsSearchFragment<MessagesSearchPresenter, IMessagesSearchView, Message, MessagesAdapter>
        implements MessagesAdapter.OnMessageActionListener, IMessagesSearchView {

    public static MessagesSearchFragment newInstance(int accountId, @Nullable MessageSeachCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        MessagesSearchFragment fragment = new MessagesSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(MessagesAdapter adapter, List<Message> data) {
        adapter.setItems(data);
    }

    @Override
    void postCreate(View root) {

    }

    @Override
    MessagesAdapter createAdapter(List<Message> data) {
        MessagesAdapter adapter = new MessagesAdapter(requireActivity(), data, this, true);
        //adapter.setOnHashTagClickListener(this);
        adapter.setOnMessageActionListener(this);
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity());
    }

    @Override
    public void onAvatarClick(@NonNull Message message, int userId) {
        getPresenter().fireOwnerClick(userId);
    }

    @Override
    public void onLongAvatarClick(@NonNull Message message, int userId) {
        getPresenter().fireOwnerClick(userId);
    }

    @Override
    public void onRestoreClick(@NonNull Message message, int position) {
        // delete is not supported
    }

    @Override
    public void onBotKeyboardClick(@NonNull @NotNull Keyboard.Button button) {
        // is not supported
    }

    @Override
    public boolean onMessageLongClick(@NonNull Message message) {
        return false;
    }

    @Override
    public void onMessageClicked(@NonNull Message message) {
        getPresenter().fireMessageClick(message);
    }

    @Override
    public void onMessageDelete(@NonNull Message message) {

    }

    @NotNull
    @Override
    public IPresenterFactory<MessagesSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            MessageSeachCriteria c = getArguments().getParcelable(Extra.CRITERIA);
            return new MessagesSearchPresenter(accountId, c, saveInstanceState);
        };
    }

    @Override
    public void goToMessagesLookup(int accountId, int peerId, int messageId) {
        PlaceFactory.getMessagesLookupPlace(accountId, peerId, messageId, null).tryOpenWith(requireActivity());
    }
}

package dev.idm.vkp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.ChatMembersListDomainAdapter;
import dev.idm.vkp.fragment.base.BaseMvpBottomSheetDialogFragment;
import dev.idm.vkp.model.AppChatUser;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.ChatUsersDomainPresenter;
import dev.idm.vkp.mvp.view.IChatUsersDomainView;
import dev.idm.vkp.view.MySearchView;

import static dev.idm.vkp.util.Objects.nonNull;

public class ChatUsersDomainFragment extends BaseMvpBottomSheetDialogFragment<ChatUsersDomainPresenter, IChatUsersDomainView>
        implements IChatUsersDomainView, ChatMembersListDomainAdapter.ActionListener {

    private ChatMembersListDomainAdapter mAdapter;
    private Listener listener;

    private static Bundle buildArgs(int accountId, int chatId) {
        Bundle args = new Bundle();
        args.putInt(Extra.CHAT_ID, chatId);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    public static ChatUsersDomainFragment newInstance(int accountId, int chatId, Listener listener) {
        ChatUsersDomainFragment fragment = new ChatUsersDomainFragment();
        fragment.listener = listener;
        fragment.setArguments(buildArgs(accountId, chatId));
        return fragment;
    }

    @Override
    @NotNull
    public BottomSheetDialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), getTheme());
        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        behavior.setSkipCollapsed(true);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_chat_users_domain, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        MySearchView mySearchView = root.findViewById(R.id.searchview);
        mySearchView.setRightButtonVisibility(false);
        mySearchView.setLeftIcon(R.drawable.magnify);
        mySearchView.setOnQueryTextListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getPresenter().fireQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getPresenter().fireQuery(newText);
                return false;
            }
        });

        mAdapter = new ChatMembersListDomainAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(this);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void displayData(List<AppChatUser> users) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(users);
        }
    }

    @Override
    public void notifyItemRemoved(int position) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void openUserWall(int accountId, Owner user) {
        if (nonNull(listener)) {
            listener.onSelected(user);
        }
    }

    @Override
    public void displayRefreshing(boolean refreshing) {

    }

    @NotNull
    @Override
    public IPresenterFactory<ChatUsersDomainPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatUsersDomainPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getInt(Extra.CHAT_ID),
                saveInstanceState
        );
    }

    @Override
    public void onUserClick(AppChatUser user) {
        getPresenter().fireUserClick(user);
    }

    interface Listener {
        void onSelected(Owner user);
    }
}

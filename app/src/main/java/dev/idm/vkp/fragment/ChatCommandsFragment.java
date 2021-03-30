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
import dev.idm.vkp.adapter.ChatCommandsListAdapter;
import dev.idm.vkp.fragment.base.BaseMvpBottomSheetDialogFragment;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.ChatCommandsPresenter;
import dev.idm.vkp.mvp.view.IChatCommandsView;
import dev.idm.vkp.view.MySearchView;

import static dev.idm.vkp.util.Objects.nonNull;

public class ChatCommandsFragment extends BaseMvpBottomSheetDialogFragment<ChatCommandsPresenter, IChatCommandsView>
        implements IChatCommandsView, ChatCommandsListAdapter.ActionListener {

    private ChatCommandsListAdapter mAdapter;
    private Listener listener;

    private static Bundle buildArgs(int accountId, int chatId) {
        Bundle args = new Bundle();
        args.putInt(Extra.CHAT_ID, chatId);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    public static ChatCommandsFragment newInstance(int accountId, int chatId, Listener listener) {
        ChatCommandsFragment fragment = new ChatCommandsFragment();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_chat_commands, container, false);

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

        mAdapter = new ChatCommandsListAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setActionListener(this);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void displayData(List<Command> commands) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(commands);
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
    public void displayRefreshing(boolean refreshing) {

    }

    @Override
    public void openCommand(Command command) {
        if (nonNull(listener)) {
            listener.onSelected(command);
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatCommandsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatCommandsPresenter(saveInstanceState);
    }

    @Override
    public void onCommandClick(Command command) {
        getPresenter().fireCommandClick(command);
        this.onDestroyView();
    }


    interface Listener {
        void onSelected(Command command);
    }
}

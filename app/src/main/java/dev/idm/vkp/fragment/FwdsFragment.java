package dev.idm.vkp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.adapter.AttachmentsViewBinder;
import dev.idm.vkp.adapter.MessagesAdapter;
import dev.idm.vkp.fragment.base.PlaceSupportMvpFragment;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.model.Keyboard;
import dev.idm.vkp.model.LastReadId;
import dev.idm.vkp.model.Message;
import dev.idm.vkp.model.VoiceMessage;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.FwdsPresenter;
import dev.idm.vkp.mvp.view.IFwdsView;

import static dev.idm.vkp.util.Objects.nonNull;

public class FwdsFragment extends PlaceSupportMvpFragment<FwdsPresenter, IFwdsView>
        implements MessagesAdapter.OnMessageActionListener, IFwdsView, AttachmentsViewBinder.VoiceActionListener {

    private MessagesAdapter mAdapter;

    public static Bundle buildArgs(int accountId, ArrayList<Message> messages) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelableArrayList(Extra.MESSAGES, messages);
        return args;
    }

    public static FwdsFragment newInstance(Bundle args) {
        FwdsFragment fwdsFragment = new FwdsFragment();
        fwdsFragment.setArguments(args);
        return fwdsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fwds, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mAdapter = new MessagesAdapter(requireActivity(), Collections.emptyList(), this, true);
        mAdapter.setOnMessageActionListener(this);
        mAdapter.setVoiceActionListener(this);
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onClearSelection();
        }

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setSubtitle(null);
            actionBar.setTitle(R.string.title_messages);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void onAvatarClick(@NonNull Message message, int userId) {
        onOpenOwner(userId);
    }

    @Override
    public void onLongAvatarClick(@NonNull Message message, int userId) {
        onOpenOwner(userId);
    }

    @Override
    public void onRestoreClick(@NonNull Message message, int position) {
        // not supported
    }

    @Override
    public void onBotKeyboardClick(@NonNull @NotNull Keyboard.Button button) {
        // not supported
    }

    @Override
    public boolean onMessageLongClick(@NonNull Message message) {
        // not supported
        return false;
    }

    @Override
    public void onMessageClicked(@NonNull Message message) {
        // not supported
    }

    @Override
    public void onMessageDelete(@NonNull Message message) {

    }

    @Override
    public void displayMessages(@NonNull List<Message> messages, @NonNull LastReadId lastReadId) {
        if (nonNull(mAdapter)) {
            mAdapter.setItems(messages, lastReadId);
        }
    }

    @Override
    public void notifyMessagesUpAdded(int position, int count) {
        // not supported
    }

    @Override
    public void notifyDataChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyMessagesDownAdded(int count) {
        // not supported
    }

    @Override
    public void configNowVoiceMessagePlaying(int voiceId, float progress, boolean paused, boolean amin) {
        if (nonNull(mAdapter)) {
            mAdapter.configNowVoiceMessagePlaying(voiceId, progress, paused, amin);
        }
    }

    @Override
    public void bindVoiceHolderById(int holderId, boolean play, boolean paused, float progress, boolean amin) {
        if (nonNull(mAdapter)) {
            mAdapter.bindVoiceHolderById(holderId, play, paused, progress, amin);
        }
    }

    @Override
    public void disableVoicePlaying() {
        if (nonNull(mAdapter)) {
            mAdapter.disableVoiceMessagePlaying();
        }
    }

    @Override
    public void showActionMode(String title, Boolean canEdit, Boolean canPin, Boolean canStar, Boolean doStar) {
        // not supported
    }

    @Override
    public void finishActionMode() {
        // not supported
    }

    @NotNull
    @Override
    public IPresenterFactory<FwdsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            ArrayList<Message> messages = requireArguments().getParcelableArrayList(Extra.MESSAGES);
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);
            return new FwdsPresenter(accountId, messages, saveInstanceState);
        };
    }

    @Override
    public void onVoiceHolderBinded(int voiceMessageId, int voiceHolderId) {
        getPresenter().fireVoiceHolderCreated(voiceMessageId, voiceHolderId);
    }

    @Override
    public void onVoicePlayButtonClick(int voiceHolderId, int voiceMessageId, @NonNull VoiceMessage voiceMessage) {
        getPresenter().fireVoicePlayButtonClick(voiceHolderId, voiceMessageId, voiceMessage);
    }

    @Override
    public void onTranscript(String voiceMessageId, int messageId) {
        getPresenter().fireTranscript(voiceMessageId, messageId);
    }
}

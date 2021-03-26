package dev.idm.vkp.fragment.conversation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.VideosAdapter;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.conversations.ChatAttachmentVideoPresenter;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentVideoView;

public class ConversationVideosFragment extends AbsChatAttachmentsFragment<Video, ChatAttachmentVideoPresenter, IChatAttachmentVideoView>
        implements VideosAdapter.VideoOnClickListener, IChatAttachmentVideoView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int columns = getResources().getInteger(R.integer.videos_column_count);
        return new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public RecyclerView.Adapter<?> createAdapter() {
        VideosAdapter adapter = new VideosAdapter(requireActivity(), Collections.emptyList());
        adapter.setVideoOnClickListener(this);
        return adapter;
    }

    @Override
    public void onVideoClick(int position, Video video) {
        getPresenter().fireVideoClick(video);
    }

    @Override
    public boolean onVideoLongClick(int position, Video video) {
        getPresenter().fireGoToMessagesLookup(video.getMsgPeerId(), video.getMsgId());
        return true;
    }

    @Override
    public void displayAttachments(List<Video> data) {
        VideosAdapter adapter = (VideosAdapter) getAdapter();
        adapter.setData(data);
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatAttachmentVideoPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int peerId = getArguments().getInt(Extra.PEER_ID);
            return new ChatAttachmentVideoPresenter(peerId, accountId, saveInstanceState);
        };
    }
}

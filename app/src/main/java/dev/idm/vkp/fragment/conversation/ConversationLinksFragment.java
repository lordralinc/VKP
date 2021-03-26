package dev.idm.vkp.fragment.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.adapter.LinksAdapter;
import dev.idm.vkp.model.Link;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.conversations.ChatAttachmentLinksPresenter;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentLinksView;

public class ConversationLinksFragment extends AbsChatAttachmentsFragment<Link, ChatAttachmentLinksPresenter, IChatAttachmentLinksView>
        implements LinksAdapter.ActionListener, LinksAdapter.LinkConversationListener, IChatAttachmentLinksView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
    }

    @Override
    public RecyclerView.Adapter<?> createAdapter() {
        LinksAdapter simpleDocRecycleAdapter = new LinksAdapter(Collections.emptyList());
        simpleDocRecycleAdapter.setActionListener(this);
        simpleDocRecycleAdapter.setLinkConversationListener(this);
        return simpleDocRecycleAdapter;
    }

    @Override
    public void displayAttachments(List<Link> data) {
        if (getAdapter() instanceof LinksAdapter) {
            ((LinksAdapter) getAdapter()).setItems(data);
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatAttachmentLinksPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatAttachmentLinksPresenter(
                getArguments().getInt(Extra.PEER_ID),
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    public void onLinkClick(int index, @NonNull Link link) {
        getPresenter().fireLinkClick(link);
    }

    @Override
    public void onGoLinkConversation(@NonNull Link doc) {
        getPresenter().fireGoToMessagesLookup(doc.getMsgPeerId(), doc.getMsgId());
    }
}

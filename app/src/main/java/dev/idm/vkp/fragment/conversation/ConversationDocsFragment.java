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
import dev.idm.vkp.adapter.DocsAdapter;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.conversations.ChatAttachmentDocsPresenter;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentDocsView;

public class ConversationDocsFragment extends AbsChatAttachmentsFragment<Document, ChatAttachmentDocsPresenter, IChatAttachmentDocsView>
        implements DocsAdapter.ActionListener, IChatAttachmentDocsView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
    }

    @Override
    public RecyclerView.Adapter<?> createAdapter() {
        DocsAdapter simpleDocRecycleAdapter = new DocsAdapter(Collections.emptyList());
        simpleDocRecycleAdapter.setActionListener(this);
        return simpleDocRecycleAdapter;
    }

    @Override
    public void displayAttachments(List<Document> data) {
        if (getAdapter() instanceof DocsAdapter) {
            ((DocsAdapter) getAdapter()).setItems(data);
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatAttachmentDocsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatAttachmentDocsPresenter(
                getArguments().getInt(Extra.PEER_ID),
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    public void onDocClick(int index, @NonNull Document doc) {
        getPresenter().fireDocClick(doc);
    }

    @Override
    public boolean onDocLongClick(int index, @NonNull Document doc) {
        getPresenter().fireGoToMessagesLookup(doc.getMsgPeerId(), doc.getMsgId());
        return true;
    }
}

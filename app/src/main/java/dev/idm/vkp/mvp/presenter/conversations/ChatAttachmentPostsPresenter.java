package dev.idm.vkp.mvp.presenter.conversations;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.model.VKApiAttachment;
import dev.idm.vkp.api.model.VKApiLink;
import dev.idm.vkp.api.model.response.AttachmentsHistoryResponse;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.model.Link;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentPostsView;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;

public class ChatAttachmentPostsPresenter extends BaseChatAttachmentsPresenter<Link, IChatAttachmentPostsView> {

    public ChatAttachmentPostsPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @Override
    Single<Pair<String, List<Link>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, VKApiAttachment.TYPE_POST, nextFrom, 50, null)
                .map(response -> {
                    List<Link> docs = new ArrayList<>(safeCountOf(response.items));

                    if (nonNull(response.items)) {
                        for (AttachmentsHistoryResponse.One one : response.items) {
                            if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiLink) {
                                VKApiLink dto = (VKApiLink) one.entry.attachment;
                                docs.add(Dto2Model.transform(dto).setMsgId(one.messageId).setMsgPeerId(peerId));
                            }
                        }
                    }

                    return Pair.Companion.create(response.next_from, docs);
                });
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.posts_count, safeCountOf(data)));
        }
    }
}

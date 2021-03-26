package dev.idm.vkp.mvp.presenter.conversations;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.model.VKApiVideo;
import dev.idm.vkp.api.model.response.AttachmentsHistoryResponse;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.model.Video;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentVideoView;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;

public class ChatAttachmentVideoPresenter extends BaseChatAttachmentsPresenter<Video, IChatAttachmentVideoView> {

    public ChatAttachmentVideoPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @Override
    Single<Pair<String, List<Video>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, "video", nextFrom, 50, null)
                .map(response -> {
                    List<Video> videos = new ArrayList<>(safeCountOf(response.items));

                    if (nonNull(response.items)) {
                        for (AttachmentsHistoryResponse.One one : response.items) {
                            if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiVideo) {
                                VKApiVideo dto = (VKApiVideo) one.entry.attachment;
                                videos.add(Dto2Model.transform(dto).setMsgId(one.messageId).setMsgPeerId(peerId));
                            }
                        }
                    }

                    return Pair.Companion.create(response.next_from, videos);
                });
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.videos_count, safeCountOf(data)));
        }
    }
}

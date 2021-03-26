package dev.idm.vkp.mvp.presenter.conversations;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.model.VKApiAudio;
import dev.idm.vkp.api.model.response.AttachmentsHistoryResponse;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.model.Audio;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentAudiosView;
import dev.idm.vkp.util.Pair;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;

public class ChatAttachmentAudioPresenter extends BaseChatAttachmentsPresenter<Audio, IChatAttachmentAudiosView> {

    public ChatAttachmentAudioPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @Override
    Single<Pair<String, List<Audio>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, "audio", nextFrom, 50, null)
                .map(response -> {
                    List<Audio> audios = new ArrayList<>(safeCountOf(response.items));

                    if (nonNull(response.items)) {
                        for (AttachmentsHistoryResponse.One one : response.items) {
                            if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiAudio) {
                                VKApiAudio dto = (VKApiAudio) one.entry.attachment;
                                audios.add(Dto2Model.transform(dto));
                            }
                        }
                    }

                    return Pair.Companion.create(response.next_from, audios);
                });
    }

    @SuppressWarnings("unused")
    public void fireAudioPlayClick(int position, Audio audio) {
        fireAudioPlayClick(position, new ArrayList<>(data));
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.audios_count, safeCountOf(data)));
        }
    }
}
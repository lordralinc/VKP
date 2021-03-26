package dev.idm.vkp.mvp.presenter.conversations;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.model.VKApiPhoto;
import dev.idm.vkp.api.model.response.AttachmentsHistoryResponse;
import dev.idm.vkp.db.Stores;
import dev.idm.vkp.db.serialize.Serializers;
import dev.idm.vkp.domain.mappers.Dto2Model;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.TmpSource;
import dev.idm.vkp.mvp.reflect.OnGuiCreated;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentPhotosView;
import dev.idm.vkp.util.Analytics;
import dev.idm.vkp.util.DisposableHolder;
import dev.idm.vkp.util.Pair;
import dev.idm.vkp.util.RxUtils;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;

public class ChatAttachmentPhotoPresenter extends BaseChatAttachmentsPresenter<Photo, IChatAttachmentPhotosView> {

    private final DisposableHolder<Void> openGalleryDisposableHolder = new DisposableHolder<>();

    public ChatAttachmentPhotoPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    Single<Pair<String, List<Photo>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, "photo", nextFrom, 50, null)
                .map(response -> {
                    List<Photo> photos = new ArrayList<>();

                    for (AttachmentsHistoryResponse.One one : response.items) {
                        if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiPhoto) {
                            VKApiPhoto dto = (VKApiPhoto) one.entry.attachment;
                            photos.add(Dto2Model.transform(dto).setMsgId(one.messageId).setMsgPeerId(peerId));
                        }
                    }

                    return Pair.Companion.create(response.next_from, photos);
                });
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.photos_count, safeCountOf(data)));
        }
    }

    @Override
    public void onDestroyed() {
        openGalleryDisposableHolder.dispose();
        super.onDestroyed();
    }

    @SuppressWarnings("unused")
    public void firePhotoClick(int position, Photo photo) {
        List<Photo> photos = data;

        TmpSource source = new TmpSource(getInstanceId(), 0);

        fireTempDataUsage();

        openGalleryDisposableHolder.append(Stores.getInstance()
                .tempStore()
                .put(source.getOwnerId(), source.getSourceId(), data, Serializers.PHOTOS_SERIALIZER)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(() -> onPhotosSavedToTmpStore(position, source), Analytics::logUnexpectedError));
    }

    private void onPhotosSavedToTmpStore(int index, TmpSource source) {
        callView(view -> view.goToTempPhotosGallery(getAccountId(), source, index));
    }
}

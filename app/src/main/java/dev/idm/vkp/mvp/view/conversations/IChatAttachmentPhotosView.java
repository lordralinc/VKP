package dev.idm.vkp.mvp.view.conversations;

import androidx.annotation.NonNull;

import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.TmpSource;


public interface IChatAttachmentPhotosView extends IBaseChatAttachmentsView<Photo> {
    void goToTempPhotosGallery(int accountId, @NonNull TmpSource source, int index);
}
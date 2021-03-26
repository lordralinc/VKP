package dev.idm.vkp.db.interfaces;

import dev.idm.vkp.crypt.KeyLocationPolicy;

public interface IStorages {

    ITempDataStorage tempStore();

    IVideoAlbumsStorage videoAlbums();

    IVideoStorage videos();

    IAttachmentsStorage attachments();

    IKeysStorage keys(@KeyLocationPolicy int policy);

    ILocalMediaStorage localMedia();

    IFeedbackStorage notifications();

    IDialogsStorage dialogs();

    IMessagesStorage messages();

    IWallStorage wall();

    IFaveStorage fave();

    IPhotosStorage photos();

    IRelativeshipStorage relativeship();

    IFeedStorage feed();

    IOwnersStorage owners();

    ICommentsStorage comments();

    IPhotoAlbumsStorage photoAlbums();

    ITopicsStore topics();

    IDocsStorage docs();

    IStickersStorage stickers();

    IDatabaseStore database();
}
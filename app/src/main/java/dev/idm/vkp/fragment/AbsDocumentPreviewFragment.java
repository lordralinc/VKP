package dev.idm.vkp.fragment;

import android.Manifest;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;

import dev.idm.vkp.R;
import dev.idm.vkp.activity.SendAttachmentsActivity;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.model.Document;
import dev.idm.vkp.model.EditingPostType;
import dev.idm.vkp.mvp.presenter.BaseDocumentPresenter;
import dev.idm.vkp.mvp.view.IBasicDocumentView;
import dev.idm.vkp.place.PlaceUtil;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.Utils;

public abstract class AbsDocumentPreviewFragment<P extends BaseDocumentPresenter<V>, V
        extends IBasicDocumentView> extends BaseMvpFragment<P, V> implements IBasicDocumentView {

    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> {
                if (isPresenterPrepared()) {
                    getPresenter().fireWritePermissionResolved(requireActivity(), requireView());
                }
            });

    @Override
    public void requestWriteExternalStoragePermission() {
        requestWritePermission.launch();
    }

    @Override
    public void shareDocument(int accountId, @NonNull Document document) {
        String[] items = {
                getString(R.string.share_link),
                getString(R.string.repost_send_message),
                getString(R.string.repost_to_wall)
        };

        new MaterialAlertDialogBuilder(requireActivity())
                .setItems(items, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            Utils.shareLink(requireActivity(), document.generateWebLink(), document.getTitle());
                            break;
                        case 1:
                            SendAttachmentsActivity.startForSendAttachments(requireActivity(), accountId, document);
                            break;
                        case 2:
                            PlaceUtil.goToPostCreation(requireActivity(), accountId, accountId, EditingPostType.TEMP, Collections.singletonList(document));
                            break;
                    }
                })
                .setCancelable(true)
                .setTitle(R.string.share_document_title)
                .show();
    }
}

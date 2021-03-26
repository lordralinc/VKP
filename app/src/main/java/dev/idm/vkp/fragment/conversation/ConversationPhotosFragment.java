package dev.idm.vkp.fragment.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.fave.FavePhotosAdapter;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.TmpSource;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.conversations.ChatAttachmentPhotoPresenter;
import dev.idm.vkp.mvp.view.conversations.IChatAttachmentPhotosView;
import dev.idm.vkp.place.PlaceFactory;

public class ConversationPhotosFragment extends AbsChatAttachmentsFragment<Photo, ChatAttachmentPhotoPresenter,
        IChatAttachmentPhotosView> implements FavePhotosAdapter.PhotoSelectionListener, FavePhotosAdapter.PhotoConversationListener, IChatAttachmentPhotosView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int columns = getResources().getInteger(R.integer.photos_column_count);
        return new GridLayoutManager(requireActivity(), columns);
    }

    @Override
    public RecyclerView.Adapter<?> createAdapter() {
        FavePhotosAdapter apiPhotoFavePhotosAdapter = new FavePhotosAdapter(requireActivity(), Collections.emptyList());
        apiPhotoFavePhotosAdapter.setPhotoSelectionListener(this);
        apiPhotoFavePhotosAdapter.setPhotoConversationListener(this);
        return apiPhotoFavePhotosAdapter;
    }

    @Override
    public void onPhotoClicked(int position, Photo photo) {
        getPresenter().firePhotoClick(position, photo);
    }

    @NotNull
    @Override
    public IPresenterFactory<ChatAttachmentPhotoPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int peerId = getArguments().getInt(Extra.PEER_ID);
            return new ChatAttachmentPhotoPresenter(peerId, accountId, saveInstanceState);
        };
    }

    @Override
    public void displayAttachments(List<Photo> data) {
        FavePhotosAdapter adapter = (FavePhotosAdapter) getAdapter();
        adapter.setData(data);
    }

    @Override
    public void goToTempPhotosGallery(int accountId, @NonNull TmpSource source, int index) {
        PlaceFactory.getTmpSourceGalleryPlace(accountId, source, index).tryOpenWith(requireActivity());
    }

    @Override
    public void onGoPhotoConversation(@NonNull Photo photo) {
        getPresenter().fireGoToMessagesLookup(photo.getMsgPeerId(), photo.getMsgId());
    }
}
